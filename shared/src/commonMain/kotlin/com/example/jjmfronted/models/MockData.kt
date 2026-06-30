package com.example.jjmfronted.models

enum class UserRole { STUDENT, COMPANY, ADMIN }

data class MockCompany(
    val id: Int,
    val name: String,
    val industry: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val logoUrl: String = ""
)

data class MockVacancy(
    val id: Int,
    val companyId: Int,
    val companyName: String,
    val title: String,
    val description: String,
    val requirements: String,
    val area: String,
    val location: String,
    val duration: String,
    val schedule: String,
    val slots: Int,
    val appliedCount: Int = 0,
    val isActive: Boolean = true
)

data class MockApplication(
    val id: Int,
    val vacancyId: Int,
    val vacancyTitle: String,
    val companyName: String,
    val studentName: String,
    val status: ApplicationStatus,
    val appliedDate: String
)

enum class ApplicationStatus { PENDING, ACCEPTED, REJECTED }

data class MockAttendance(
    val date: String,
    val status: AttendanceStatus,
    val justification: String = ""
)

enum class AttendanceStatus { PRESENT, ABSENT, JUSTIFIED }

data class MockDocument(
    val id: Int,
    val name: String,
    val type: String,
    val uploadedBy: String,
    val uploadDate: String,
    val studentName: String = ""
)

object MockData {

    val companies = listOf(
        MockCompany(1, "TechSolutions MX", "Tecnología", "Blvd. Adolfo López Mateos 101, Cd. Victoria", 23.7369, -99.1412,
            "Empresa líder en desarrollo de software en Tamaulipas."),
        MockCompany(2, "Hospital General", "Salud", "Av. Hidalgo 500, Cd. Victoria", 23.7300, -99.1500,
            "Hospital público con programas de servicio social."),
        MockCompany(3, "Despacho Contable RG", "Finanzas", "Calle 5 de Mayo 200, Cd. Victoria", 23.7280, -99.1380,
            "Despacho contable con más de 15 años de experiencia."),
        MockCompany(4, "Constructora del Noreste", "Construcción", "Carretera Interejidal Km 2, Cd. Victoria", 23.7450, -99.1300,
            "Empresa constructora con proyectos en todo el estado."),
        MockCompany(5, "Laboratorios Farma", "Farmacéutica", "Av. Universidad 800, Cd. Victoria", 23.7220, -99.1600,
            "Laboratorio farmacéutico con programas de prácticas."),
        MockCompany(6, "Bufete Jurídico AS", "Legal", "Calle 12 150, Cd. Victoria", 23.7350, -99.1450,
            "Bufete jurídico especializado en derecho corporativo.")
    )

    val vacancies = listOf(
        MockVacancy(1, 1, "TechSolutions MX", "Desarrollador Web Jr.", "Apoyo en desarrollo de aplicaciones web con tecnologías modernas.",
            "Conocimientos básicos en HTML, CSS, JavaScript. (No excluyente)", "Informática", "Cd. Victoria, Tamps.", "6 meses", "L-V 8:00-13:00", 3, 5),
        MockVacancy(2, 1, "TechSolutions MX", "Soporte Técnico", "Mantenimiento y soporte a equipos de cómputo.",
            "Interés en soporte técnico y redes.", "Informática", "Cd. Victoria, Tamps.", "4 meses", "L-V 14:00-18:00", 2, 2),
        MockVacancy(3, 2, "Hospital General", "Auxiliar Administrativo", "Apoyo en el área de administración del hospital.",
            "Conocimientos básicos de office.", "Administración", "Cd. Victoria, Tamps.", "6 meses", "L-V 7:00-13:00", 4, 8),
        MockVacancy(4, 2, "Hospital General", "Asistente de Enfermería", "Apoyo en áreas de enfermería y consulta externa.",
            "Estudios en enfermería o afín.", "Salud", "Cd. Victoria, Tamps.", "6 meses", "L-V 8:00-14:00", 5, 3),
        MockVacancy(5, 3, "Despacho Contable RG", "Auxiliar Contable", "Captura de facturas, organización de documentación contable.",
            "Conocimientos básicos de contabilidad.", "Contabilidad", "Cd. Victoria, Tamps.", "6 meses", "L-V 9:00-14:00", 2, 1),
        MockVacancy(6, 4, "Constructora del Noreste", "Auxiliar de Obra", "Apoyo en supervisión de obra y elaboración de planos.",
            "Interés en construcción y dibujo técnico.", "Construcción", "Cd. Victoria, Tamps.", "6 meses", "L-V 7:00-15:00", 3, 0),
        MockVacancy(7, 5, "Laboratorios Farma", "Asistente de Laboratorio", "Apoyo en análisis y control de calidad.",
            "Estudios en química o afín.", "Química", "Cd. Victoria, Tamps.", "4 meses", "L-V 8:00-13:00", 2, 4),
        MockVacancy(8, 6, "Bufete Jurídico AS", "Auxiliar Jurídico", "Apoyo en elaboración de documentos legales y archivo.",
            "Interés en derecho y administración.", "Derecho", "Cd. Victoria, Tamps.", "6 meses", "L-V 9:00-15:00", 2, 0)
    )

    val applications = listOf(
        MockApplication(1, 1, "Desarrollador Web Jr.", "TechSolutions MX", "Mario Suárez", ApplicationStatus.ACCEPTED, "15/06/2026"),
        MockApplication(2, 3, "Auxiliar Administrativo", "Hospital General", "Mario Suárez", ApplicationStatus.PENDING, "18/06/2026"),
        MockApplication(3, 5, "Auxiliar Contable", "Despacho Contable RG", "Mario Suárez", ApplicationStatus.REJECTED, "10/06/2026")
    )

    val attendance = listOf(
        MockAttendance("24/06/2026", AttendanceStatus.PRESENT),
        MockAttendance("25/06/2026", AttendanceStatus.PRESENT),
        MockAttendance("26/06/2026", AttendanceStatus.ABSENT, "Problema de transporte"),
        MockAttendance("27/06/2026", AttendanceStatus.JUSTIFIED, "Cita médica"),
        MockAttendance("28/06/2026", AttendanceStatus.PRESENT)
    )

    val documents = listOf(
        MockDocument(1, "Formato de Servicio Social", "PDF", "Vinculación", "01/06/2026"),
        MockDocument(2, "Carta de Presentación", "PDF", "Vinculación", "01/06/2026"),
        MockDocument(3, "Formato de Evaluación", "PDF", "Vinculación", "15/06/2026"),
        MockDocument(4, "Reporte Parcial - Mario Suárez", "PDF", "Mario Suárez", "20/06/2026"),
        MockDocument(5, "Reporte Final - Mario Suárez", "PDF", "Mario Suárez", "28/06/2026")
    )

    val deliveries = listOf(
        MockDocument(6, "Reporte Parcial", "PDF", "Juan López", "22/06/2026"),
        MockDocument(7, "Reporte Final", "PDF", "María García", "27/06/2026"),
        MockDocument(8, "Carta Término", "PDF", "Ana Martínez", "25/06/2026")
    )
}
