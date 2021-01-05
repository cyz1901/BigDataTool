namespace java pers.cyz.BigDataTool.Master.ThirftService
struct HadoopDownloadModle {
    1:string name,
    2:map<string,string> hadoopMap
}
service HadoopDownloadService {
    HadoopDownloadModle get(1: string name)
}