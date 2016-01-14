evaluate(new File("release-commons.gvy"))

def env = System.getenv()

releaseVersion = env['RELEASE_VERSION']
releaseBranch = "release-" + releaseVersion
developmentVersion = env['DEVELOPMENT_VERSION']
releaseFromBranch = env['RELEASE_FROM_BRANCH']

new GroovyShell().parse( new File( 'release-commons.gvy' ) ).with {
	git('checkout ' + releaseFromBranch)
	git('pull origin ' + releaseFromBranch)
	git('branch ' + releaseBranch)
	mvn('versions:set -DnewVersion=' + developmentVersion)
	git('add pom.xml')
	runCommand(["git", "commit", "-m", "version updated to " + developmentVersion])
	//git('push origin ' + releaseFromBranch)
	git('checkout ' + releaseBranch)
	mvn('versions:set -DnewVersion=' + releaseVersion)
	git('add pom.xml')
	runCommand(["git", "commit", "-m", "version updated to " + releaseVersion])
	//git('push origin ' + releaseBranch)
}
