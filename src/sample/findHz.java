package sample;

import javax.sound.sampled.*;
import java.time.Duration;
import java.time.Instant;

public class findHz
{
    int DEF_BUFFER_SAMPLE_SZ;
    AudioFormat format;
    DataLine.Info info;
    TargetDataLine line;

    float[] samples;
    long[] transfer;
    byte[] bytes;

    findHz(int DEF_BUFFER_SAMPLE_SZ, AudioFormat format, DataLine.Info info)
    {
        this.DEF_BUFFER_SAMPLE_SZ = DEF_BUFFER_SAMPLE_SZ;
        this.format = format;
        this.info = info;

        try
        {
            initialize();
        }
        catch (LineUnavailableException e)
        {

        }
    }

    public void initialize() throws LineUnavailableException
    {
        final int normalBytes = Fft.normalBytesFromBits(format.getSampleSizeInBits());

        samples = new float[DEF_BUFFER_SAMPLE_SZ * format.getChannels()];
        transfer = new long[samples.length];
        bytes = new byte[samples.length * normalBytes];

        line = (TargetDataLine) AudioSystem.getLine(info);
    }

    public void capture() throws LineUnavailableException
    {
        line.open(format);
        line.start();

        System.out.println("Starting capturing");
        line.read(bytes, 0, bytes.length);
        System.out.println("Capturing ended");
        line.close();
        System.out.println("Line closed.");
    }

    public void calculateFFT()
    {
        Instant start = Instant.now();

        Object[] fft = Fft.fft(bytes, transfer, samples, bytes.length, format);

        double[] real = (double[]) fft[0];
        double[] imag = (double[]) fft[1];

        Object[] polarCoord = Fft.convertToPolar(real, imag);

        double[] MagX = (double[]) polarCoord[0];
        double[] PhaseX = (double[]) polarCoord[1];

        int[] peaksIndex = findPeaks(MagX);

        Instant end = Instant.now();

        Duration timeElapsed = Duration.between(start, end);

        System.out.println("Here are the 5 peaks: \n");

        for(int i : peaksIndex)
            System.out.println(i);

        System.out.println("This opertaion took " + timeElapsed.toMillis() + "ms with sample size of " + DEF_BUFFER_SAMPLE_SZ);
    }

    public int[] findPeaks(double[] MagX)
    {

        int[] peaks = new int[5];

        for(int i = 0; i < 5; i++)
        {
            double peak = -1.0;
            int index = -1;

            for(int j = 0; j < MagX.length; j++)
            {
                if(MagX[j] > peak)
                {
                    peak = MagX[j];
                    index = j;
                }
            }

            peaks[i] = index;
        }

        return peaks;
    }
}
