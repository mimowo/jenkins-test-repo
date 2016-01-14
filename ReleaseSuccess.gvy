evaluate(new File("ReleaseCommons.gvy"))

def env = System.getenv()

releaseVersion = env['RELEASE_VERSION']
releaseBranch = "release-" + releaseVersion
developmentVersion = env['DEVELOPMENT_VERSION']
releaseFromBranch = env['RELEASE_FROM_BRANCH']

new GroovyShell().parse( new File( 'ReleaseCommons.gvy' ) ).with {
  git('checkout ' + releaseFromBranch)
  git('push origin ' + releaseFromBranch)
  git('checkout ' + releaseBranch)
  runCommand(["git", "tag", "-a", releaseVersion, "-m", "Tag for release"])
  git('push origin ' + releaseBranch + ' --tags')
}