def env = System.getenv()

releaseVersion = env['RELEASE_VERSION']
releaseBranch = "release-" + releaseVersion
releaseTag = releaseVersion
developmentVersion = env['DEVELOPMENT_VERSION']
releaseFromBranch = env['RELEASE_FROM_BRANCH']

(git_cmd, mvn_cmd, gradle_cmd) = ["git", "mvn", "gradle"]

// a wrapper closure around executing a string                                  
// can take either a string or a list of strings (for arguments with spaces)    
// prints all output, complains and halts on error                              
def runCommand(strList) {
  print "[INFO] ( "
  if(strList instanceof List) {
    strList.each { print "${it} " }
  } else {
    print strList
  }
  println " )"

  def proc = strList.execute()
  proc.in.eachLine { line -> println line }
  proc.out.close()
  proc.waitFor()

  if (proc.exitValue()) {
    throw new RuntimeException("Failed to execute command with error: ${proc.getErrorStream()}")
  }
  return proc.exitValue()
}

def git(args, printError = true) {
  runCommand("git" + " " + args, printError)
}

def mvn(args) {
  runCommand("sh mvn" + " " + args, printError)
}

def gradle(args) {
  runCommand("gradle" + " " + args, printError)
}

def verifyTagDoesntExist() {
  try {
    git('ls-remote --tags --exit-code origin ' + releaseTag)
    //git('ls-remote --heads --exit-code origin ' + releaseBranch)
  } (RuntimeException e) {
    println "Tag " + releaseTag + " does not exist yet."
  }
  throw RuntimeException("Tag " + releaseTag + " already exists!")
}

def action = this.args[0]

if(action == 'prepare-maven') {
  verifyTagDoesntExist()

  git('checkout ' + releaseFromBranch)
  git('pull origin ' + releaseFromBranch)
  git('branch ' + releaseBranch)
  mvn('versions:set -DnewVersion=' + developmentVersion + '-DgenerateBackupPoms=false')
  git('add .')
  runCommand(["git", "commit", "-m", "version updated to " + developmentVersion])
  git('checkout ' + releaseBranch)
  mvn('versions:set -DnewVersion=' + releaseVersion + '-DgenerateBackupPoms=false')
  git('add .') 
  runCommand(["git", "commit", "-m", "version updated to " + releaseVersion])
} else if (action == 'success') {
  git("tag " + releaseTag + " " +releaseBranch)
  git('push origin ' + releaseFromBranch + ':' + releaseFromBranch)
  git('push origin ' + releaseBranch + ':' + releaseBranch + ' --tags')
}
