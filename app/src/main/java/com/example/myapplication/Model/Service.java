package com.example.myapplication.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Service {
    private String name;
    private int price;
    private String image;

    public static List<Service> serviceList = new ArrayList<>();
    public Service( String name, int price, String image){
        this.setName(name);
        this.setPrice(price);
        this.setImage(image);

    }
    public static List<Service> getInstance() {
        return serviceList;
    }
    public static List<Service> getInstance(List<Service> services) {
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

    public String getImage() {
        return image;
    }

    public String toString(){
        return name + " " + price + " " + image;
    }
    public void setImage(String imageSource) {
        this.image = imageSource;
    }
    //   nameIncreasingSort
    public static void nameIncreasingSort(List<Service> services){
        Service[] servicesArray = services.toArray(new Service[services.size()]);
        Arrays.sort(servicesArray, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        services.clear();
        services.addAll(Arrays.asList(servicesArray));
    }
    //    nameDecreasingSort
    public static void nameDecreasingSort(List<Service> services){
        Service[] servicesArray = services.toArray(new Service[services.size()]);
        Arrays.sort(servicesArray, (o1, o2) -> o2.getName().compareToIgnoreCase(o1.getName()));
        services.clear();
        services.addAll(Arrays.asList(servicesArray));
    }
    //    priceIncreasingSort
    public static void priceIncreasingSort(List<Service> services){
        Service[] servicesArray = services.toArray(new Service[services.size()]);
        Arrays.sort(servicesArray, (o1, o2) -> o1.getPrice() - o2.getPrice());
        services.clear();
        services.addAll(Arrays.asList(servicesArray));
    }
    //    priceDecreasingSort
    public static void priceDecreasingSort(List<Service> services) {
        Service[] servicesArray = services.toArray(new Service[services.size()]);
        Arrays.sort(servicesArray, (o1, o2) -> o2.getPrice() - o1.getPrice());
        services.clear();
        services.addAll(Arrays.asList(servicesArray));
    }
}

