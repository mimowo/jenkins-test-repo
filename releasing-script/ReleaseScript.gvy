
def env = System.getenv()

releaseVersion = env['RELEASE_VERSION']
releaseBranch = "release-" + releaseVersion
releaseTag = releaseVersion
developmentVersion = env['DEVELOPMENT_VERSION']
releaseFromBranch = env['RELEASE_FROM_BRANCH']
defaultBuildBranch = "master"

git_cmd = "git"

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

def git(args) {
  runCommand(git_cmd + " " + args)
}

def deleteLocalReleaseBranchIfNeeded(localBranchName) {
  try {
    git('rev-parse --verify ' + localBranchName)
  } catch (all) {
    println "[INFO] Local branch " + localBranchName + " does not exist, continue."
    return null
  }
  println "[INFO] Local branch " + localBranchName + " exits, removing."
  git('branch -D ' + releaslocalBranchNameeBranch)
}

def verifyTagDoesntExist() {
  try {
    git('ls-remote --tags --exit-code origin ' + releaseTag)
    //git('ls-remote --heads --exit-code origin ' + releaseBranch)
  } catch (RuntimeException e) {
    println "[INFO] Tag " + releaseTag + " does not exist yet, continue."
    return null
  }
  throw new RuntimeException("Tag " + releaseTag + " already exists!")
}

def createReleaseBranch() {
  if (releaseFromBranch != defaultBuildBranch) {
    git('checkout ' + releaseFromBranch)
  }
  git('branch ' + releaseBranch)
  git('pull origin ' + releaseFromBranch)
}

def commitAndCheckoutReleaseBranch() {
  git('add .')
  runCommand(["git", "commit", "-m", "Development version updated to " + developmentVersion])
  git('checkout ' + releaseBranch)
}

def commitReleaseBranch() {
  git('add .')
  runCommand(["git", "commit", "-m", "Release version updated to " + releaseVersion])
  git("tag " + releaseTag + " " +releaseBranch)
}

def pushTagsAndBranches() {
  git('push origin ' + releaseFromBranch + ':' + releaseFromBranch)
  git('push origin ' + releaseBranch + ':' + releaseBranch + ' --tags')
}

def action = this.args[0]

if (action == 'verify-and-create-release-branch') {
  verifyTagDoesntExist()

  deleteLocalReleaseBranchIfNeeded(releaseBranch)
  createReleaseBranch();
} else if (action == 'commit-current-and-checkout-release-branch') {
  commitAndCheckoutReleaseBranch();
} else if (action == 'commit-release-branch') {
  commitReleaseBranch();
} else if (action == 'after-build-success') {
  pushTagsAndBranches();
}
