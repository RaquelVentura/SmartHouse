package com.example.smarthouse.ui.views.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smarthouse.R;
import com.example.smarthouse.data.models.Alarma;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportesFragment extends Fragment {
    private static final int REQUEST_CODE_CREATE_PDF = 1001;

    private BarChart chartGasHumo, chartApertura;
    private TextView tvResumenAlarmas, tvResumenApertura;
    private DatabaseReference alarmasRef;
    private Map<String, Integer> conteoGasHumo, conteoApertura;
    private LineChart chartTemperatura;
    private TextView tvResumenTemperatura;
    private DatabaseReference temperaturaRef;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportes, container, false);

        chartGasHumo = view.findViewById(R.id.chartAlarmas);
        chartApertura = view.findViewById(R.id.chartApertura);
        tvResumenAlarmas = view.findViewById(R.id.tvResumenAlarmas);
        tvResumenApertura = view.findViewById(R.id.tvResumenApertura);
        chartTemperatura = view.findViewById(R.id.chartTemperatura);
        tvResumenTemperatura = view.findViewById(R.id.tvResumenTemperatura);
        temperaturaRef = FirebaseDatabase.getInstance().getReference("graficaTemperaturaHumedad");

        Button btnExportarPDF = view.findViewById(R.id.btnExportarPDF);

        alarmasRef = FirebaseDatabase.getInstance().getReference("alarmas");
        btnExportarPDF.setOnClickListener(v -> exportarPDF());

        obtenerDatosDesdeFirebase();
        obtenerDatosTemperatura();
        return view;
    }

    private void obtenerDatosDesdeFirebase() {
        Calendar hoy = Calendar.getInstance();
        Calendar hace7Dias = Calendar.getInstance();
        hace7Dias.add(Calendar.DAY_OF_YEAR, -6);

        SimpleDateFormat formatoFirebase = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat formatoVisible = new SimpleDateFormat("dd MMM", Locale.getDefault());

        conteoGasHumo = inicializarMapaFechas(hace7Dias, formatoVisible);
        conteoApertura = inicializarMapaFechas(hace7Dias, formatoVisible);

        alarmasRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot nodo : snapshot.getChildren()) {
                    Alarma alarma = nodo.getValue(Alarma.class);
                    if (alarma != null && alarma.getTipoEvento() != null) {
                        try {
                            Date fechaAlarma = formatoFirebase.parse(alarma.getFecha());
                            if (fechaAlarma != null && !fechaAlarma.before(hace7Dias.getTime()) && !fechaAlarma.after(hoy.getTime())) {
                                String claveVisible = formatoVisible.format(fechaAlarma);
                                String tipo = alarma.getTipoEvento();

                                if (tipo.contains("GAS") || tipo.contains("HUMO")) {
                                    conteoGasHumo.put(claveVisible, conteoGasHumo.getOrDefault(claveVisible, 0) + 1);
                                }
                                if (tipo.equals("APERTURA_MODO_SEGURO")) {
                                    conteoApertura.put(claveVisible, conteoApertura.getOrDefault(claveVisible, 0) + 1);
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mostrarGrafica(chartGasHumo, conteoGasHumo, "Gas/Humo", tvResumenAlarmas);
                mostrarGrafica(chartApertura, conteoApertura, "Apertura segura", tvResumenApertura);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Map<String, Integer> inicializarMapaFechas(Calendar desde, SimpleDateFormat formatoVisible) {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        for (int i = 0; i < 7; i++) {
            Calendar dia = (Calendar) desde.clone();
            dia.add(Calendar.DAY_OF_YEAR, i);
            mapa.put(formatoVisible.format(dia.getTime()), 0);
        }
        return mapa;
    }

    private void mostrarGrafica(BarChart chart, Map<String, Integer> conteoPorDia, String titulo, TextView tvResumen) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();
        int index = 0;

        for (Map.Entry<String, Integer> entry : conteoPorDia.entrySet()) {
            entries.add(new BarEntry(index, entry.getValue()));
            etiquetas.add(entry.getKey());
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Alarmas de " + titulo);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        BarData barData = new BarData(dataSet);

        chart.setData(barData);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(etiquetas));
        chart.getXAxis().setGranularity(1f);
        chart.getXAxis().setGranularityEnabled(true);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setText("Frecuencia diaria");

        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        int colorTexto = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) ? Color.WHITE : Color.BLACK;

        chart.getXAxis().setTextColor(colorTexto);
        chart.getAxisLeft().setTextColor(colorTexto);
        chart.getAxisRight().setTextColor(colorTexto);
        dataSet.setValueTextColor(colorTexto);
        chart.getLegend().setTextColor(colorTexto);
        chart.getDescription().setTextColor(colorTexto);

        chart.invalidate();

        // Resumen
        int maxValor = 0;
        String diaMax = "";
        for (Map.Entry<String, Integer> entry : conteoPorDia.entrySet()) {
            if (entry.getValue() > maxValor) {
                maxValor = entry.getValue();
                diaMax = entry.getKey();
            }
        }
        String resumen = maxValor == 0 ?
                "No se registraron alarmas esta semana." :
                "Día con más alarmas de " + titulo + ": " + diaMax + " (" + maxValor + ")";

        tvResumen.setText(resumen);
    }

    private void obtenerDatosTemperatura() {
        temperaturaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, List<Float>> temperaturasPorDia = new LinkedHashMap<>();

                for (DataSnapshot nodo : snapshot.getChildren()) {
                    String fecha = nodo.child("fecha").getValue(String.class);
                    String tempStr = nodo.child("temperatura").getValue(String.class);

                    if (fecha != null && tempStr != null) {
                        float temp = Float.parseFloat(tempStr);
                        temperaturasPorDia.putIfAbsent(fecha, new ArrayList<>());
                        temperaturasPorDia.get(fecha).add(temp);
                    }
                }

                Map<String, Float> promedioPorDia = new LinkedHashMap<>();
                for (Map.Entry<String, List<Float>> entry : temperaturasPorDia.entrySet()) {
                    float suma = 0;
                    for (Float t : entry.getValue()) suma += t;
                    promedioPorDia.put(entry.getKey(), suma / entry.getValue().size());
                }

                mostrarGraficaTemperatura(chartTemperatura, promedioPorDia);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error al obtener temperaturas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarGraficaTemperatura(LineChart chart, Map<String, Float> datos) {
        List<Entry> entries = new ArrayList<>();
        List<String> etiquetas = new ArrayList<>();
        int index = 0;

        float alpha = 0.5f;  // Suavizado exponencial simple
        Float pronostico = null;

        for (Map.Entry<String, Float> entry : datos.entrySet()) {
            entries.add(new Entry(index, entry.getValue()));
            etiquetas.add(entry.getKey());

            // Calcular suavizado exponencial
            if (pronostico == null) {
                pronostico = entry.getValue();  // primer dato como pronóstico inicial
            } else {
                pronostico = alpha * entry.getValue() + (1 - alpha) * pronostico;
            }

            index++;
        }

        LineDataSet dataSet = new LineDataSet(entries, "Promedio Diario de Temperatura");
        dataSet.setColor(Color.BLUE);
        dataSet.setValueTextSize(12f);
        dataSet.setCircleColor(Color.RED);
        dataSet.setLineWidth(2f);

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(etiquetas));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        chart.getAxisRight().setEnabled(false);

        Description desc = new Description();
        desc.setText("Temperatura promedio por día");
        chart.setDescription(desc);
        chart.getLegend().setEnabled(true);

        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        int colorTexto = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) ? Color.WHITE : Color.BLACK;

        chart.getXAxis().setTextColor(colorTexto);
        chart.getAxisLeft().setTextColor(colorTexto);
        chart.getAxisRight().setTextColor(colorTexto);
        dataSet.setValueTextColor(colorTexto);
        chart.getLegend().setTextColor(colorTexto);
        chart.getDescription().setTextColor(colorTexto);

        chart.invalidate();

        tvResumenTemperatura.setText(String.format(Locale.getDefault(),
                "Predicción para el próximo día: %.1f °C", pronostico));
    }

    private void prepararGraficoParaExportar(Chart<?> chart) {
        if (chart instanceof BarChart) {
            BarDataSet dataSet = (BarDataSet) ((BarChart) chart).getData().getDataSetByIndex(0);
            dataSet.setValueTextColor(Color.BLACK);
        } else if (chart instanceof LineChart) {
            LineDataSet dataSet = (LineDataSet) ((LineChart) chart).getData().getDataSetByIndex(0);
            dataSet.setValueTextColor(Color.BLACK);
        }

        // Cast para acceder a ejes (solo válido en gráficos de barras y líneas)
        if (chart instanceof BarLineChartBase<?>) {
            BarLineChartBase<?> chartEjes = (BarLineChartBase<?>) chart;
            chartEjes.getXAxis().setTextColor(Color.BLACK);
            chartEjes.getAxisLeft().setTextColor(Color.BLACK);
            chartEjes.getAxisRight().setTextColor(Color.BLACK); // por si acaso
        }

        chart.getLegend().setTextColor(Color.BLACK);
        chart.getDescription().setTextColor(Color.BLACK);
        chart.invalidate();
    }


    private void exportarPDF() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "reporte_alarmas.pdf");
        startActivityForResult(intent, REQUEST_CODE_CREATE_PDF);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CREATE_PDF && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            generarPDF(data.getData());
        }
    }

    private void generarPDF(Uri uri) {
        try {
            ParcelFileDescriptor pfd = getContext().getContentResolver().openFileDescriptor(uri, "w");
            FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());

            Document document = new Document();
            PdfWriter.getInstance(document, fos);
            document.open();

            Font titulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font subtitulo = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            Font textoNormal = new Font(Font.FontFamily.HELVETICA, 12);

            // Título principal
            document.add(new Paragraph("Reporte de Alarmas – Smart House", titulo));
            document.add(new Paragraph("Fecha de generación: " +
                    new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()), textoNormal));
            document.add(Chunk.NEWLINE);

            // Sección Gas/Humo
            document.add(new Paragraph("Gráfico – Alarmas Gas/Humo", subtitulo));
            prepararGraficoParaExportar(chartGasHumo);
            Image imgGas = Image.getInstance(chartToByteArray(chartGasHumo));
            imgGas.scaleToFit(400, 250);
            imgGas.setAlignment(Image.ALIGN_CENTER);
            document.add(imgGas);
            document.add(new Paragraph(tvResumenAlarmas.getText().toString(), textoNormal));
            document.add(Chunk.NEWLINE);

            // Sección Apertura Segura
            document.add(new Paragraph("Gráfico – Apertura Segura", subtitulo));
            prepararGraficoParaExportar(chartApertura);
            Image imgApertura = Image.getInstance(chartToByteArray(chartApertura));
            imgApertura.scaleToFit(400, 250);
            imgApertura.setAlignment(Image.ALIGN_CENTER);
            document.add(imgApertura);
            document.add(new Paragraph(tvResumenApertura.getText().toString(), textoNormal));
            document.add(Chunk.NEWLINE);

            // Tabla resumen
            document.add(new Paragraph("Resumen por día:", subtitulo));
            PdfPTable table = new PdfPTable(3); // 3 columnas
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.addCell("Día");
            table.addCell("Gas/Humo");
            table.addCell("Aperturas");

            for (String dia : conteoGasHumo.keySet()) {
                table.addCell(dia);
                table.addCell(String.valueOf(conteoGasHumo.get(dia)));
                table.addCell(String.valueOf(conteoApertura.get(dia)));
            }

            document.add(table);

            document.add(new Paragraph("Gráfico – Temperatura promedio por día", subtitulo));
            prepararGraficoParaExportar(chartTemperatura);
            Image imgTemp = Image.getInstance(chartToByteArray(chartTemperatura));
            imgTemp.scaleToFit(400, 250);
            imgTemp.setAlignment(Image.ALIGN_CENTER);
            document.add(imgTemp);
            document.add(new Paragraph(tvResumenTemperatura.getText().toString(), textoNormal));
            document.add(Chunk.NEWLINE);

            document.close();
            fos.close();

            Toast.makeText(getContext(), "PDF exportado exitosamente", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al exportar PDF", Toast.LENGTH_SHORT).show();
        }
    }


    private byte[] chartToByteArray(Chart<?> chart) {
        chart.setDrawingCacheEnabled(true);
        chart.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(chart.getDrawingCache());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        chart.setDrawingCacheEnabled(false);
        return stream.toByteArray();
    }

}
