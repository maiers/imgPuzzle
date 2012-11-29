// Fraunhofer Institute for Computer Graphics Research (IGD)
// Competence Center for Information Visualization and Visual Analytics
//
// Copyright (c) 2011-2012 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.
package de.comci.imgp.ui;

import java.awt.Dimension;

/**
 *
 * @author Sebastian Maier
 */
public class ImageUtil {

    public static double getScaleFactorToFit(Dimension original, Dimension toFit) {

        double dScale = 1d;

        if (original != null && toFit != null) {

            double dScaleWidth = getScaleFactor(original.width, toFit.width);
            double dScaleHeight = getScaleFactor(original.height, toFit.height);

            dScale = Math.min(dScaleHeight, dScaleWidth);

        }

        return dScale;

    }

    public static double getScaleFactorToFill(Dimension masterSize, Dimension targetSize) {

        double dScaleWidth = getScaleFactor(masterSize.width, targetSize.width);
        double dScaleHeight = getScaleFactor(masterSize.height, targetSize.height);

        double dScale = Math.max(dScaleHeight, dScaleWidth);

        return dScale;

    }

    public static double getScaleFactor(int iMasterSize, int iTargetSize) {

        double dScale = 1;
        if (iMasterSize > iTargetSize) {

            dScale = (double) iTargetSize / (double) iMasterSize;

        } else {

            dScale = (double) iTargetSize / (double) iMasterSize;

        }

        return dScale;

    }
    
}