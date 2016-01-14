def env = System.getenv()

releaseVersion = env['RELEASE_VERSION']
releaseBranch = "release-" + releaseVersion
developmentVersion = env['DEVELOPMENT_VERSION']
releaseFromBranch = env['RELEASE_FROM_BRANCH']

new GroovyShell().parse( new File( 'ReleaseCommons.gvy' ) ).with {
  assert !git("checkout " + releaseFromBranch)
  assert !git("reset --hard HEAD~1")
  assert !git('branch -d ' + releaseBranch)
}
