package hicupp.algorithms;

import hicupp.*;
import interactivehicupp.TextTools;

public final class Simplex {
    /**
     * Maximize a function using the Simplex method of Nelder and Mead.
     * <p>Reference:<br />
     * <i>A Simplex method of function minimization.<br />
     * The Computer Journal<br />
     * Vol. 7, p. 308, 1964.</i></p>
     * <p>Author:<br />
     * Art Smith<br />
     * IFAS Statistics<br />
     * 410 Rolfs Hall<br />
     * University of Florida<br />
     * Gainesville, FL 32611</p>
     * <p>Note:<br />
     * This routine is a symmetric reversal of Nelders published
     * algorithm which has been written for the projection pursuit problem.</p>
     * @param monitor If not <code>null</code>, this object will be notified
     *                of milestones within the computation. The object is also
     *                given a chance to cancel the computation.
     * @return An argument list for which the function is (sufficiently) maximal.
     * @exception NoConvergenceException If the algorithm fails to find a maximum.
     * @exception CancellationException Passed through from the <code>monitor</code>'s
     * {@link Monitor#continuing()} method.
     */
    public static double[] maximize(Function function, Monitor monitor)
            throws NoConvergenceException, CancellationException {
        final double RFACT = 1.0;
        final double CFACT  = 0.5;
        final double EFACT  = 2.0;
        final double EDGLEN = 5.0e-1;
        final double PFACT1 = 1.0e-2;

        final MonitoringFunctionWrapper wrapper =
                new MonitoringFunctionWrapper(new CloningFunctionWrapper(function), monitor);
        final int n = function.getArgumentCount();
        final int np1 = n + 1;

        double cscale = CFACT;
        double done = PFACT1;
        double escale = EFACT;
        double rscale = RFACT;
        double side = EDGLEN;

        // Force parameters to valid values if necessary.

        if (cscale <= 0.0 || cscale >= 1.0)
            cscale = 0.5;
        if (done <= 0.0)
            done = 1.0e-3;
        if (escale <= 1.0)
            escale = 2.0;
        if (rscale <= 0.0)
            rscale = 1.0;
        if (side <= 0.0)
            side = 0.1;

        // Compute initial simplex.
        final double[][] x = new double[np1][n];
        for (int i = 0; i < np1; i++) {
			/*
      double xlim = 1;
      for (int j = 0; j < n; j++) {
        x[i][j] = (2 * Math.random() - 1) * Math.sqrt(xlim);
        xlim -= x[i][j] * x[i][j];
      }
			*/
            double sumsq = 0;
            for (int j = 0; j < n; j++) {
                double v = 2 * Math.random() - 1;
                sumsq += v * v;
                x[i][j] = v;
            }
            double norm = Math.sqrt(sumsq);
            for (int j = 0; j < n; j++)
                x[i][j] /= norm;
        }


        // Compute function values.

        double[] fx = new double[np1];
        for (int i = 0; i < np1; i++)
            fx[i] = wrapper.evaluate(x[i]);

        int iter = 0;
        while (true) {
            iter++;

            for (int i = 0; i < x.length; i++) {
                double[] y = x[i];
                for (int j = 0; j < y.length; j++)
                    System.out.print(y[j] + " ");
                System.out.println(" => " + fx[i]);
            }
            System.out.println();

      /*
      if (iter > 10000)
        throw new NoConvergenceException("Maximum iteration count exceeded.");
      */

            if (monitor != null) {
                monitor.continuing();
                monitor.iterationStarted(iter);
            }

            // Determine minimum and maximum values.

            double fxmax = fx[0];
            double fxmin = fx[0];
            int high = 0;
            int low = 0;

            for (int i = 0; i < np1; i++) {
                if (fx[i] < fxmin) {
                    fxmin = fx[i];
                    low = i;
                }
                if (fx[i] > fxmax) {
                    fxmax = fx[i];
                    high = i;
                }
            }

            // Compute the current centroid.

            double[] xcent = new double[n];
            for (int j = 0; j < n; j++) {
                double sum = 0.0;
                for (int i = 0; i < np1; i++)
                    sum += x[i][j];
                sum -= x[low][j];
                xcent[j] = sum / n;
            }
            double fxcent = wrapper.evaluate(xcent);

            // Compute the reflected point.

            double[] xref = new double[n];
            for (int j = 0; j < n; j++)
                xref[j] = xcent[j] + rscale * (xcent[j] - x[low][j]);
            double fxref = wrapper.evaluate(xref);

            // Replace worst point x[low][j] with best new point.

            if (fxref > fxmax) {

                // Compute an expansion point.
                double[] xexp = new double[n];
                for (int j = 0; j < n; j++)
                    xexp[j] = xcent[j] + escale * (xref[j] - xcent[j]);
                double fxexp = wrapper.evaluate(xexp);

                if (fxexp > fxref) {
                    for (int j = 0; j < n; j++)
                        x[low][j] = xexp[j];
                    fx[low] = fxexp;
                } else {
                    for (int j = 0; j < n; j++)
                        x[low][j] = xref[j];
                    fx[low] = fxref;
                }
            } else {
                boolean stop = false;
                for (int i = 0; i < np1; i++) {
                    if (fxref > fx[i] && i != low) {
                        stop = true;
                        break;
                    }
                }
                if (stop) {
                    for (int j = 0; j < n; j++)
                        x[low][j] = xref[j];
                    fx[low] = fxref;
                } else {
                    if (fxref > fxmin) {

                        // We have a new worst point.

                        for (int j = 0; j < n; j++)
                            x[low][j] = xref[j];
                        fx[low] = fxref;
                        fxmin = fxref;
                    }

                    // Compute a contraction point.

                    double[] xcon = new double[n];
                    for (int j = 0; j < n; j++)
                        xcon[j] = xcent[j] + cscale * (x[low][j] - xcent[j]);
                    double fxcon = wrapper.evaluate(xcon);

                    if (fxcon < fxmin) {
                        for (int j = 0; j < n; j++) {
                            for (int i = 0; i < np1; i++) {
                                if (i != high)
                                    x[i][j] = (x[i][j] + x[high][j]) * 0.5;
                            }
                        }
                        for (int i = 0; i < np1; i++) {
                            if (i != high)
                                fx[i] = wrapper.evaluate(x[i]);
                        }
                    } else {
                        for (int j = 0; j < n; j++)
                            x[low][j] = xcon[j];
                        fx[low] = fxcon;
                    }
                }
            }

            // Have we reached an acceptable maximum?

            double convrg = 2.0 * Math.abs(fx[high] - fx[low]) /
                    (Math.abs(fx[high]) + Math.abs(fx[low]));
//      if (convrg <= done)

            if (monitor != null)
                monitor.writeLine("(iter = " + iter +
                        ") (fx[high] = " + TextTools.formatScientific(fx[high]) +
                        ") (convrg = " + TextTools.formatScientific(convrg) +
                        ") (x[high] = {" + AlgorithmUtilities.argumentArrayToString(x[high]) + "})");

            if (convrg <= 1e-4)
                break;
        }

        int k = 1;
        double f = fx[1];
        for (int i = 0; i < np1; i++) {
            if (fx[i] > f) {
                f = fx[i];
                k = i;
            }
        }

        System.out.println("Optimal value: " + f);

        return x[k];
    }
}
