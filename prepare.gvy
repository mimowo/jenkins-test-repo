def env = System.getenv()

releseVersion = env['RELEASE_VERSION']
developmentVersion = env['DEVELOPMENT_VERSION']
releaseFromBranch = env['RELEASE_FROM_BRANCH']
(git_cmd, mvn_cmd, gradle_cmd) = ["git", "mvn", "gradle"]

println(git_cmd)

def run(cmd) {
	println('Command: ' + cmd)
	def proc = cmd.execute()
	//proc.waitForOrKill(10000)
	proc.text.eachLine {println it}
	if (proc.exitValue()) {
		println "Command failed!"
	}
	//assert !proc.exitValue()
}

def git(args) {
	run(git_cmd + " " + args)
}

def mvn(args) {
	run(mvn_cmd + " " + args)
}

def gradle(args) {
	run(gradle_cmd + " " + args)
}

git('checkout ' + releaseFromBranch)
git('pull')
git('branch ' + releseVersion)
mvn('versions:set -DnewVersion=' + developmentVersion)
git('add pom.xml')
git('commit -m "version updated to ' + developmentVersion + '"')
git('push')
git('checkout ' + releseVersion)
mvn('versions:set -DnewVersion=' + releseVersion)
git('add pom.xml')
git('commit -m "version updated to ' + developmentVersion + '"')
git('push')
