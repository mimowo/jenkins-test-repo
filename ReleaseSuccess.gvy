def env = System.getenv()

releaseVersion = env['RELEASE_VERSION']
releaseBranch = "release-" + releaseVersion
developmentVersion = env['DEVELOPMENT_VERSION']
releaseFromBranch = env['RELEASE_FROM_BRANCH']

new GroovyShell().parse( new File( 'ReleaseCommons.gvy' ) ).with {
  git('push origin ' + releaseFromBranch + ':' + releaseFromBranch)
  git("tag " + releaseVersion + " " +releaseBranch)
  git('push origin ' + releaseBranch + ':' + releaseBranch + ' --tags')
}