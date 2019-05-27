package com.example.barchart;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

interface AsyncResponse {
    void processFinish(String output);
}
class Data extends AsyncTask<Void,Void,String> {
String ur="";
    AsyncResponse delegate = null;

    @Override
    protected String doInBackground(Void... voids) {
        try {
            ur = "http://competencyservices.herokuapp.com/cmptncy/fetchCount";
            URL url = new URL(ur);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            int response = httpURLConnection.getResponseCode();

            if (response == HttpURLConnection.HTTP_OK) {
                ur = "";
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    ur += line;
                }
                return ur;
            }
            else
            {
                return response+" "+ur;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return ur+"URL";
        } catch (IOException e) {
            e.printStackTrace();
            return ur+"IO";
        }

    }

    @Override
    protected void onPostExecute(String aVoid) {

        delegate.processFinish(aVoid);
    }

}

public class MainActivity extends AppCompatActivity implements AsyncResponse {
BarChart barChart;
TextView t;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        barChart=findViewById(R.id.barchart);
        t=findViewById(R.id.t);
        Data d=new Data();
        d.delegate=this;
        d.execute();
    }

    @Override
    public void processFinish(String aVoid) {


        ObjectMapper mapper = new ObjectMapper();
        try {
            Count count[] = mapper.readValue(aVoid, Count[].class);

            BarDataSet barDataSet1=new BarDataSet(b1(count,"BASIC"),"Basic");
            barDataSet1.setColor(Color.RED);
            BarDataSet barDataSet2=new BarDataSet(b1(count,"ADVANCED"),"Advanced");
            barDataSet2.setColor(Color.GREEN);
            BarDataSet barDataSet3=new BarDataSet(b1(count,"PROFICIENT"),"Proficient");
            barDataSet3.setColor(Color.BLUE);

            BarData data=new BarData(barDataSet1,barDataSet2,barDataSet3);

            barChart.setData(data);

            String[] x={"C01","C02","C03","C04","C05","C06","C07","C09","C10","C11","C12","C13"};
            XAxis xAxis=barChart.getXAxis();
            xAxis.setValueFormatter(new IndexAxisValueFormatter(x));
            xAxis.setCenterAxisLabels(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(barChart.getXAxis().getAxisMaximum() /(float) (x.length));

            xAxis.setGranularityEnabled(true);
            barChart.setDragEnabled(true);
            barChart.setVisibleXRangeMaximum(3);
            int xzx;
            float barspace=0.03f,groupspace=0.4f;
            data.setBarWidth(0.2f);
            barChart.getXAxis().setAxisMinimum(0);
            barChart.getXAxis().setAxisMaximum(0+barChart.getBarData().getGroupWidth(groupspace,barspace)*13);
            barChart.groupBars(0,groupspace,barspace);

            barChart.invalidate();





        } catch (IOException e) {
            e.printStackTrace();
            barChart.setVisibility(View.GONE);
            t.setText("ERROR");
            t.setVisibility(View.VISIBLE);

        } catch (NullPointerException e) {
            e.printStackTrace();
            barChart.setVisibility(View.GONE);
            t.setText("ERROR");
            t.setVisibility(View.VISIBLE);

        }

    }

    ArrayList<BarEntry> b1(Count count[],String s){

        ArrayList<BarEntry> x=new ArrayList<>();
        int z=1;
        String ss=t.getText().toString();
        for(int i=0;i<count.length;i++) {

            if(count[i].cmptncyLevel.equals(s)) {
                x.add(new BarEntry(z++, count[i].cmptncyCount));
            ss+="added "+(z-1)+" "+count[i].cmptncyLevel+" "+count[i].cmptncyCount+"\n";
            }

        }
        ss+="\n"+z+s+(count[0].getCmptncyLevel()==s)+(count[0].getCmptncyLevel().equals(s))+"OVER\n";
//        barChart.setVisibility(View.GONE);
//        t.setText(ss);
//        t.setVisibility(View.VISIBLE);

     //   if(s=="ADVANCED"||s=="PROFICIENT")
   // x.add(new BarEntry(14,0));
        return x;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}


