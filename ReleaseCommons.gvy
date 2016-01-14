(git_cmd, mvn_cmd, gradle_cmd) = ["git", "mvn", "gradle"]

// a wrapper closure around executing a string                                  
// can take either a string or a list of strings (for arguments with spaces)    
// prints all output, complains and halts on error                              
def runCommand(strList, printError = true) {
  print "[INFO] ( "
  if(strList instanceof List) {
    strList.each { print "${it} " }
  } else {
    print strList
  }
  println " )"

  def proc = strList.execute()
  proc.in.eachLine { line -> println line }
  proc.out.close()
  proc.waitFor()

  if (proc.exitValue() && printError) {
    println "gave the following error: "
    println "[ERROR] ${proc.getErrorStream()}"
  }
  return proc.exitValue()
}

def git(args, printError = true) {
	runCommand("git" + " " + args, printError)
}

def mvn(args) {
	runCommand("sh " + "mvn" + " " + args, printError)
}

def gradle(args) {
	runCommand("gradle" + " " + args, printError)
}
