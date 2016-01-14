def env = System.getenv()

releaseVersion = env['RELEASE_VERSION']
releaseBranch = "release-" + releaseVersion
developmentVersion = env['DEVELOPMENT_VERSION']
releaseFromBranch = env['RELEASE_FROM_BRANCH']

new GroovyShell().parse( new File( 'ReleaseCommons.gvy' ) ).with {
  assert !git('checkout ' + releaseFromBranch)
  assert !git('pull origin ' + releaseFromBranch)
  assert !git('branch ' + releaseBranch)
  assert !mvn('versions:set -DnewVersion=' + developmentVersion)
  assert !git('add pom.xml')
  assert !runCommand(["git", "commit", "-m", "version updated to " + developmentVersion])

  assert !git('checkout ' + releaseBranch)
  assert !mvn('versions:set -DnewVersion=' + releaseVersion) 
  assert !git('add pom.xml') 
  assert !runCommand(["git", "commit", "-m", "version updated to " + releaseVersion])
}
