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
def runCommand(strList, printError = true) {
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

  if (proc.exitValue() && printError) {
    println "gave the following error: "
    println "[ERROR] ${proc.getErrorStream()}"
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

def action = this.args[0]

if(action == 'prepare-maven') {
  assert git('ls-remote --tags --exit-code origin ' + releaseTag, printError=false)
  assert git('ls-remote --heads --exit-code origin ' + releaseBranch, printError=false)
  assert !git('checkout ' + releaseFromBranch)
  assert !git('pull origin ' + releaseFromBranch)
  assert !git('branch ' + releaseBranch)
  assert !mvn('versions:set -DnewVersion=' + developmentVersion)
  assert !git('add .')
  assert !runCommand(["git", "commit", "-m", "version updated to " + developmentVersion])
  assert !git('checkout ' + releaseBranch)
  assert !mvn('versions:set -DnewVersion=' + releaseVersion) 
  assert !git('add .') 
  assert !runCommand(["git", "commit", "-m", "version updated to " + releaseVersion])
} else if (action == 'success') {
  assert !git("tag " + releaseTag + " " +releaseBranch)
  assert !git('push origin ' + releaseFromBranch + ':' + releaseFromBranch)
  assert !git('push origin ' + releaseBranch + ':' + releaseBranch + ' --tags')
}
