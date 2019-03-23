package com.example.maya.rivalnewfinal;

/**
 * Created by Maya on 20/03/2016.
 */
import android.content.Context;
import android.location.Location;

/**
 * Created by Maya on 18/02/2016.
 */
public class DistanceTracker {

    private double distanceSum;
    GpsTracker gptr;
    Location lastLocation;
    Context context;


    public DistanceTracker(Context context1,GpsTracker gps)//peula bona
    {
        context = context1;
        gptr =gps;
        distanceSum = 0;
        lastLocation = getAvgLocation();

    }

    public Location getLastLocation() {
        return lastLocation;
    }
    public double getDistanceSum() {
        return distanceSum;
    }// return the runner sum distance

    public String[] updateDistance()//the timer peula
    {
        String [] stArr;
        gptr= new GpsTracker(context);
        Location newLocation= getAvgLocation();
        stArr = calculteDistance(lastLocation, newLocation);
        double newDistance = Double.parseDouble(stArr[1]);
        //distanceSum = distanceSum+ newDistance;
        lastLocation = newLocation;
        stArr[0]+= String.valueOf("distance final:" + distanceSum + "\n");
        String str = String.valueOf(Math.round(distanceSum));
        return stArr;
    }
    public Location getAvgLocation()
    {Location answer = gptr.getLocation();
        double longtitude=answer.getLongitude();
        double latitude= answer.getLatitude();
        for (int i=0;i<4;i++) {
            answer = gptr.getLocation();
            latitude+=answer.getLatitude();
            longtitude+=answer.getLongitude();
        }
        latitude=latitude/5;
        longtitude=longtitude/ 5;

        answer.setLatitude(latitude);
        answer.setLongitude(longtitude);
        return answer;
    }

    public String[] calculteDistance(Location location1, Location location2) {

        String [] stArr = new String[2];

        double lat1 = location1.getLatitude();
        double lat2 = location2.getLatitude();
        double lon1 = location1.getLongitude();
        double lon2 = location2.getLongitude();

        float distancefinal = -1;

        String str ="Old location:\n" +
                " Lat:"+String.valueOf(lat1)+"\n"+
                "Long:"+String.valueOf(lon1)+ "\n\n" +
                "New location:\n" +
                "Lat:"+String.valueOf(lat2)+"\n" +
                "Long:"+String.valueOf(lon2)+"\n"+
                "Accuracy1:"+String.valueOf(location1.getAccuracy())+"\n"+
                "Accuracy2:"+String.valueOf(location2.getAccuracy())+"\n";
        //distancefinal = location1.distanceTo(location2);

 double earthRadius = 6371000; //meters
 double dLat = Math.toRadians(lat2-lat1);
 double dLng = Math.toRadians(lon2-lon1);
 double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
 Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
 Math.sin(dLng/2) * Math.sin(dLng/2);
 double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
 float dist = (float) (earthRadius * c);
 distancefinal=dist;


        distanceSum+=distancefinal;

        /**  double theta = lon1 - lon2;
         double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
         dist = Math.acos(dist);
         dist = Math.toDegrees(dist);
         dist = dist * 60 * 1.1515;

         dist = dist * 1.609344*1000;
         distancefinal = dist;
         **/
        stArr [0] = str;
        stArr[1] =String.valueOf(distanceSum);
        return stArr;
    }
}