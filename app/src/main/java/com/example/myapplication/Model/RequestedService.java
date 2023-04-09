package com.example.myapplication.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestedService {
    private String name;
    private int price;
    private String status;
    private int count;

    public static List<RequestedService> serviceList = new ArrayList<>();
    public RequestedService(String name, int price, int count, String status){
        this.count = count;
        this.setName(name);
        this.setPrice(price);
        this.setStatus(status);

    }
    public static List<RequestedService> getInstance() {
        return serviceList;
    }
    public static List<RequestedService> getInstance(List<RequestedService> services) {
        if (serviceList!=null)
            serviceList.addAll(services);
        return serviceList;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public String toString(){
        return name + " " + price + " " + status;
    }
    public void setStatus(String imageSource) {
        this.status = imageSource;
    }
    //   nameIncreasingSort
    public static void nameIncreasingSort(List<RequestedService> services){
        RequestedService[] servicesArray = services.toArray(new RequestedService[services.size()]);
        Arrays.sort(servicesArray, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        services.clear();
        services.addAll(Arrays.asList(servicesArray));
    }
    //    nameDecreasingSort
    public static void nameDecreasingSort(List<RequestedService> services){
        RequestedService[] servicesArray = services.toArray(new RequestedService[services.size()]);
        Arrays.sort(servicesArray, (o1, o2) -> o2.getName().compareToIgnoreCase(o1.getName()));
        services.clear();
        services.addAll(Arrays.asList(servicesArray));
    }
    //    priceIncreasingSort
    public static void priceIncreasingSort(List<RequestedService> services){
        RequestedService[] servicesArray = services.toArray(new RequestedService[services.size()]);
        Arrays.sort(servicesArray, (o1, o2) -> o1.getPrice() - o2.getPrice());
        services.clear();
        services.addAll(Arrays.asList(servicesArray));
    }
    //    priceDecreasingSort
    public static void priceDecreasingSort(List<RequestedService> services) {
        RequestedService[] servicesArray = services.toArray(new RequestedService[services.size()]);
        Arrays.sort(servicesArray, (o1, o2) -> o2.getPrice() - o1.getPrice());
        services.clear();
        services.addAll(Arrays.asList(servicesArray));
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}

