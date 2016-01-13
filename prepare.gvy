def env = System.getenv()

releseVersion = env['RELEASE_VERSION']
developmentVersion = env['DEVELOPMENT_VERSION']
releaseFromBranch = env['RELEASE_FROM_BRANCH']
(git_cmd, mvn_cmd, gradle_cmd) = ["git", "mvn", "gradle"]

println(git_cmd)

def run(cmd) {
	println(cmd)
	println cmd.execute().text
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

git("checkout " + env['RELEASE_FROM_BRANCH'])
mvn("--batch-mode release:update-versions -DdevelopmentVersion=" + developmentVersion)
git('commit -m "version updated to ' + developmentVersion + '"')
git('push')
