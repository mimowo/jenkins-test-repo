def env = System.getenv()

println env['RELEASE_VERSION']
println env['RELEASE_FROM_BRANCH']
println env['DEVELOPMENT_VERSION']
def git_cmd	 = "git"
def mvn_cmd = "mvn"
def gradle_cmd = "gradle"

println git_cmd

def git(args) {

	(git_cmd + " " + args).execute()
}

git("checkout " + env['RELEASE_FROM_BRANCH'])
