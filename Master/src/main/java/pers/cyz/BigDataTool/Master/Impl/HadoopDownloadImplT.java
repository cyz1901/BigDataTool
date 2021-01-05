package pers.cyz.BigDataTool.Master.Impl;

import org.apache.thrift.TException;
import pers.cyz.BigDataTool.Master.Model.HadoopDownloadModle;
import pers.cyz.BigDataTool.Master.ThirftService.HadoopDownloadService;

import java.util.HashMap;

class HadoopDownloadImplT implements HadoopDownloadService.Iface{

    @Override
    public HadoopDownloadModle get(String name) throws TException {
        HashMap<String,String> hs = new HashMap<>();
        hs.put("hh","aa");
        HadoopDownloadModle mod = new HadoopDownloadModle("hadoop",hs);
        return mod;
    }
}
