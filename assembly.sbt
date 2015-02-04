import AssemblyKeys._ // put this at the top of the file

assemblySettings

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case PathList("org", "w3c", xs @ _*)         => MergeStrategy.first
    case x => old(x)
  }
}
