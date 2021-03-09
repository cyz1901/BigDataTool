package pers.cyz.bigdatatool.uiservice.pojo;

import java.util.ArrayList;

public class DownloadMsgData{
    DownloadMsgData.AllData allData;
    ArrayList<ListData> listDataList;

    public DownloadMsgData(DownloadMsgData.AllData allData, ArrayList<DownloadMsgData.ListData> listDataList) {
        this.allData = allData;
        this.listDataList = listDataList;
    }

    public DownloadMsgData.AllData getAllData() {
        return allData;
    }

    public void setAllData(DownloadMsgData.AllData allData) {
        this.allData = allData;
    }

    public ArrayList<DownloadMsgData.ListData> getListDataList() {
        return listDataList;
    }

    public void setListDataList(ArrayList<DownloadMsgData.ListData> listDataList) {
        this.listDataList = listDataList;
    }

    public static class AllData {
        int totalComponents;
        int nowComponents;
        Long totalDownloadSize;
        Long nowlDownloadSize;

        public AllData(int totalComponents, int nowComponents, Long totalDownloadSize, Long nowlDownloadSize) {
            this.totalComponents = totalComponents;
            this.nowComponents = nowComponents;
            this.totalDownloadSize = totalDownloadSize;
            this.nowlDownloadSize = nowlDownloadSize;
        }

        public int getTotalComponents() {
            return totalComponents;
        }

        public void setTotalComponents(int totalComponents) {
            this.totalComponents = totalComponents;
        }

        public int getNowComponents() {
            return nowComponents;
        }

        public void setNowComponents(int nowComponents) {
            this.nowComponents = nowComponents;
        }

        public Long getTotalDownloadSize() {
            return totalDownloadSize;
        }

        public void setTotalDownloadSize(Long totalDownloadSize) {
            this.totalDownloadSize = totalDownloadSize;
        }

        public Long getNowlDownloadSize() {
            return nowlDownloadSize;
        }

        public void setNowlDownloadSize(Long nowlDownloadSize) {
            this.nowlDownloadSize = nowlDownloadSize;
        }
    }
    public static class ListData {
        String name;
        Long totalDownloadSize;
        Long nowlDownloadSize;

        public ListData(String name, Long totalDownloadSize, Long nowlDownloadSize) {
            this.name = name;
            this.totalDownloadSize = totalDownloadSize;
            this.nowlDownloadSize = nowlDownloadSize;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getTotalDownloadSize() {
            return totalDownloadSize;
        }

        public void setTotalDownloadSize(Long totalDownloadSize) {
            this.totalDownloadSize = totalDownloadSize;
        }

        public Long getNowlDownloadSize() {
            return nowlDownloadSize;
        }

        public void setNowlDownloadSize(Long nowlDownloadSize) {
            this.nowlDownloadSize = nowlDownloadSize;
        }
    }
}