package org.dress.mydress.ImageProcess;

import org.opencv.core.Point;

/**
 * Created by user on 2017/6/3.
 */

public class ProcessData {

    public String PhotoPath;
    public Point top_left;
    public Point bottom_right;
    public int y;

    public ProcessData()
    {
        PhotoPath = null;
        top_left = new Point(0,0);
        bottom_right = new Point(0,0);
    }

    public ProcessData(String path, Point tl, Point br)
    {
        PhotoPath = path;
        top_left = tl;
        bottom_right = br;
    }
}
