package com.example.Gestion_Flotte_Automobile.service;

import java.io.ByteArrayInputStream;

public interface ReportService {
    ByteArrayInputStream generateReservationReportPdf();

    ByteArrayInputStream generateReservationReportExcel();
}
