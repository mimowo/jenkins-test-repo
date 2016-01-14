(git_cmd, mvn_cmd, gradle_cmd) = ["git", "mvn", "gradle"]

// a wrapper closure around executing a string                                  
// can take either a string or a list of strings (for arguments with spaces)    
// prints all output, complains and halts on error                              
def runCommand(strList) {
  def proc = strList.execute()
  proc.in.eachLine { line -> println line }
  proc.out.close()
  proc.waitFor()

  print "[INFO] ( "
  if(strList instanceof List) {
    strList.each { print "${it} " }
  } else {
    print strList
  }
  println " )"

  if (proc.exitValue()) {
    println "gave the following error: "
    println "[ERROR] ${proc.getErrorStream()}"
  }
  assert !proc.exitValue()
}

def git(args) {
	runCommand(git_cmd + " " + args)
}

def mvn(args) {
	runCommand("sh " + mvn_cmd + " " + args)
}

def gradle(args) {
	runCommand(gradle_cmd + " " + args)
}
