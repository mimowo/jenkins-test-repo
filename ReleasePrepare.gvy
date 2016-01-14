def env = System.getenv()

releaseVersion = env['RELEASE_VERSION']
releaseBranch = "release-" + releaseVersion
developmentVersion = env['DEVELOPMENT_VERSION']
releaseFromBranch = env['RELEASE_FROM_BRANCH']

new GroovyShell().parse( new File( 'ReleaseCommons.gvy' ) ).with {
  git('git ls-remote --heads origin ' + releaseBranch + " | wc -l")
  git('checkout ' + releaseFromBranch)
  git('pull origin ' + releaseFromBranch)
  git('branch ' + releaseBranch)
  mvn('versions:set -DnewVersion=' + developmentVersion)
  git('add .')
  runCommand(["git", "commit", "-m", "version updated to " + developmentVersion])
  git('checkout ' + releaseBranch)
  mvn('versions:set -DnewVersion=' + releaseVersion) 
  git('add .') 
  runCommand(["git", "commit", "-m", "version updated to " + releaseVersion])
}
