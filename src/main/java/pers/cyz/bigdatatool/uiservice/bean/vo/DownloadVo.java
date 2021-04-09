package pers.cyz.bigdatatool.uiservice.bean.vo;

import java.util.ArrayList;

public class DownloadVo {
    DownloadVo.AllData allData;
    ArrayList<ListData> listDataList;
    String status;

    public DownloadVo(AllData allData, ArrayList<ListData> listDataList, String status) {
        this.allData = allData;
        this.listDataList = listDataList;
        this.status = status;
    }

    public AllData getAllData() {
        return allData;
    }

    public void setAllData(AllData allData) {
        this.allData = allData;
    }

    public ArrayList<ListData> getListDataList() {
        return listDataList;
    }

    public void setListDataList(ArrayList<ListData> listDataList) {
        this.listDataList = listDataList;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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