def env = System.getenv()

releaseVersion = env['RELEASE_VERSION']
releaseBranch = "release-" + releaseVersion
releaseFromBranch = env['RELEASE_FROM_BRANCH']

new GroovyShell().parse( new File( 'ReleaseCommons.gvy' ) ).with {
  assert !git("tag " + releaseVersion + " " +releaseBranch)
  assert !git('push origin ' + releaseFromBranch + ':' + releaseFromBranch)
  assert !git('push origin ' + releaseBranch + ':' + releaseBranch + ' --tags')
}