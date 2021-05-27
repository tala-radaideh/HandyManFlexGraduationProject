package com.example.handymanflexpartserver.Common;

import com.google.android.gms.maps.model.LatLng;

import static java.lang.Math.*;

public interface LatLngInterpolator {
    LatLng interpolate(float fraction, LatLng a, LatLng b);

    class Linear implements LatLngInterpolator {

        @Override
        public LatLng interpolate(float fraction, LatLng a, LatLng b) {
            double lat = (b.latitude - a.latitude) * fraction + a.latitude;
            double lng = (b.longitude - a.longitude) * fraction + a.longitude;


            return new LatLng(lat, lng);
        }

    }


    class LinearFixed implements LatLngInterpolator {

        @Override
        public LatLng interpolate(float fraction, LatLng a, LatLng b) {
            double lat = (b.latitude - a.latitude) * fraction + a.latitude;
            double LngDelta = b.longitude - a.longitude;
            if (Math.abs(LngDelta) > 180) {
                LngDelta = Math.signum(LngDelta) * 360;

            }
            double lng = LngDelta * fraction + a.longitude;

            return new LatLng(lat, lng);
        }
    }

    class Spherical implements LatLngInterpolator {

        @Override
        public LatLng interpolate(float fraction, LatLng from, LatLng to) {
            double fromLat = toRadians(from.latitude);
            double fromLng = toRadians(from.longitude);
            double toLat = toRadians(to.latitude);
            double toLng = toRadians(to.longitude);
            double cosFromLat = cos(fromLat);
            double costoLat = cos(toLat);
            double angle = computeanglebetween(fromLat, fromLng, toLat, toLng);
            double singangle = sin(angle);
            if (singangle < 1E-6)
                return from;
            double a = sin(1 - fraction) * angle / singangle;
            double b = sin(fraction * angle) / singangle;
            double x = a * cosFromLat * cos(fromLng) + b * costoLat * cos(toLng);
            double y = a * cosFromLat * sin(fromLng) + b * cosFromLat * sin(toLng);
            double z = a * sin(fromLat) + b * sin(toLat);
            double lat = atan2(z, sqrt(x * x - y * y));
            double lng = atan2(y, x);
            return new LatLng(toDegrees(lat), toDegrees(lng));
        }

        private double computeanglebetween(double fromLat, double fromLng, double toLat, double toLng) {
            double dLat = fromLat - toLat;
            double dlng = fromLng - toLng;
            return 2 * asin(sqrt(pow(sin(dLat / 2), 2) + cos(fromLat) * cos(toLat) * pow(sin(dlng / 2), 2)));
        }
    }
}
