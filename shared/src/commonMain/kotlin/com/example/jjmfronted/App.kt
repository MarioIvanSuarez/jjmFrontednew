package com.example.jjmfronted

import androidx.compose.runtime.*
import com.example.jjmfronted.models.*
import com.example.jjmfronted.network.ApiClient
import com.example.jjmfronted.screens.*
import com.example.jjmfronted.screens.admin.AdminDeliveriesScreen
import com.example.jjmfronted.screens.admin.AdminDocumentsScreen
import com.example.jjmfronted.screens.admin.AdminHomeScreen
import com.example.jjmfronted.screens.auth.ForgotPasswordScreen
import com.example.jjmfronted.screens.auth.LoginScreen
import com.example.jjmfronted.screens.auth.RegisterScreen
import com.example.jjmfronted.screens.company.*
import com.example.jjmfronted.screens.student.*
import com.example.jjmfronted.network.NotificationApi
import com.example.jjmfronted.screens.NotificationsScreen
import com.example.jjmfronted.ui.theme.JJMTheme
import com.example.jjmfronted.models.Postulacion
import kotlinx.coroutines.launch

enum class AppScreen {
    LOGIN, REGISTER, FORGOT_PASSWORD,
    STUDENT_HOME, VACANCY_DETAIL, APPLICATIONS, STUDENT_DOCUMENTS, MAP, PROFILE,
    COMPANY_HOME, COMPANY_VACANCIES, COMPANY_REQUESTS, ATTENDANCE, COMPANY_DOCUMENTS,
    COMPANY_EDIT_PROFILE, STUDENT_PROFILE,
    ADMIN_HOME, ADMIN_DOCUMENTS, ADMIN_DELIVERIES,
    NOTIFICATIONS
}

@Composable
fun App(onUserChanged: (User?) -> Unit = {}) {
    JJMTheme {
        var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }
        var currentUser by remember { mutableStateOf<User?>(null) }
        var currentRole by remember { mutableStateOf(UserRole.STUDENT) }
        var isLoading by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf<String?>(null) }
        var selectedVacancy by remember { mutableStateOf<Vacante?>(null) }
        var selectedPostulacion by remember { mutableStateOf<Postulacion?>(null) }
        val scope = rememberCoroutineScope()

        when (currentScreen) {
            AppScreen.LOGIN -> {
                LoginScreen(
                    onLogin = { email, password ->
                        scope.launch {
                            isLoading = true
                            error = null
                            val result = ApiClient.login(email, password)
                            result.fold(
                                onSuccess = { response ->
                            val token = response.token ?: ""
                            ApiClient.setToken(token)
                            NotificationApi.setToken(token)
                            val user = response.user
                            val role = UserRole.fromApi(user.role ?: "ESTUDIANTE")
                            currentUser = user
                            currentRole = role
                            onUserChanged(user)
                            currentScreen = when (role) {
                                UserRole.STUDENT -> AppScreen.STUDENT_HOME
                                UserRole.COMPANY -> AppScreen.COMPANY_HOME
                                UserRole.ADMIN -> AppScreen.ADMIN_HOME
                                else -> AppScreen.STUDENT_HOME
                            }
                            isLoading = false
                                },
                                onFailure = { e ->
                                    error = e.message ?: "Credenciales inválidas"
                                    isLoading = false
                                }
                            )
                        }
                    },
                    onNavigateToRegister = { currentScreen = AppScreen.REGISTER; error = null },
                    onNavigateToForgotPassword = { currentScreen = AppScreen.FORGOT_PASSWORD },
                    isLoading = isLoading,
                    error = error
                )
            }

            AppScreen.REGISTER -> {
                RegisterScreen(
                    onRegister = { email, password, name, role ->
                        scope.launch {
                            isLoading = true
                            error = null
                            val result = ApiClient.register(email, password, name, role.apiValue)
                            result.fold(
                                onSuccess = { response ->
                            val token = response.token ?: ""
                            ApiClient.setToken(token)
                            NotificationApi.setToken(token)
                            val user = response.user
                            val userRole = UserRole.fromApi(user.role ?: role.apiValue)
                            currentUser = user
                            currentRole = userRole
                            onUserChanged(user)
                            currentScreen = when (userRole) {
                                UserRole.STUDENT -> AppScreen.STUDENT_HOME
                                UserRole.COMPANY -> AppScreen.COMPANY_HOME
                                UserRole.ADMIN -> AppScreen.ADMIN_HOME
                                else -> AppScreen.STUDENT_HOME
                            }
                            isLoading = false
                                },
                                onFailure = { e ->
                                    error = e.message ?: "Error al registrarse"
                                    isLoading = false
                                }
                            )
                        }
                    },
                    onNavigateToLogin = { currentScreen = AppScreen.LOGIN; error = null },
                    isLoading = isLoading,
                    error = error
                )
            }

            AppScreen.FORGOT_PASSWORD -> {
                ForgotPasswordScreen(onBack = { currentScreen = AppScreen.LOGIN })
            }

            // Student screens
            AppScreen.STUDENT_HOME -> {
                currentUser?.let { user ->
                    StudentHomeScreen(
                        user = user,
                        token = ApiClient.getToken() ?: "",
                        onVacancyClick = { vacancy ->
                            selectedVacancy = vacancy
                            currentScreen = AppScreen.VACANCY_DETAIL
                        },
                        onApplicationsClick = { currentScreen = AppScreen.APPLICATIONS },
                        onDocumentsClick = { currentScreen = AppScreen.STUDENT_DOCUMENTS },
                        onMapClick = { currentScreen = AppScreen.MAP },
                        onProfileClick = { currentScreen = AppScreen.PROFILE },
                        onNotificationsClick = { currentScreen = AppScreen.NOTIFICATIONS },
                        onLogout = {
                            currentUser = null
                            ApiClient.setToken(null)
                            NotificationApi.setToken(null)
                            onUserChanged(null)
                            currentScreen = AppScreen.LOGIN
                        }
                    )
                }
            }

            AppScreen.VACANCY_DETAIL -> {
                selectedVacancy?.let { vacancy ->
                    VacancyDetailScreen(
                        vacancy = vacancy,
                        token = ApiClient.getToken() ?: "",
                        onBack = { currentScreen = AppScreen.STUDENT_HOME },
                        onApply = { currentScreen = AppScreen.APPLICATIONS }
                    )
                }
            }

            AppScreen.APPLICATIONS -> {
                ApplicationStatusScreen(
                    token = ApiClient.getToken() ?: "",
                    onBack = { currentScreen = AppScreen.STUDENT_HOME }
                )
            }

            AppScreen.STUDENT_DOCUMENTS -> {
                StudentDocumentsScreen(
                    token = ApiClient.getToken() ?: "",
                    onBack = { currentScreen = AppScreen.STUDENT_HOME }
                )
            }

            AppScreen.MAP -> {
                MapScreen(
                    token = ApiClient.getToken() ?: "",
                    onBack = { currentScreen = AppScreen.STUDENT_HOME },
                    onVacancyClick = { vacancy ->
                        selectedVacancy = vacancy
                        currentScreen = AppScreen.VACANCY_DETAIL
                    }
                )
            }

            AppScreen.PROFILE -> {
                currentUser?.let { user ->
                    ProfileScreen(
                        user = user,
                        onBack = {
                            currentScreen = when (currentRole) {
                                UserRole.STUDENT -> AppScreen.STUDENT_HOME
                                UserRole.COMPANY -> AppScreen.COMPANY_HOME
                                UserRole.ADMIN -> AppScreen.ADMIN_HOME
                            }
                        },
                        onLogout = {
                            currentUser = null
                            ApiClient.setToken(null)
                            NotificationApi.setToken(null)
                            onUserChanged(null)
                            currentScreen = AppScreen.LOGIN
                        }
                    )
                }
            }

            // Company screens
            AppScreen.COMPANY_HOME -> {
                currentUser?.let { user ->
                    CompanyHomeScreen(
                        user = user,
                        token = ApiClient.getToken() ?: "",
                        onVacanciesClick = { currentScreen = AppScreen.COMPANY_VACANCIES },
                        onRequestsClick = { currentScreen = AppScreen.COMPANY_REQUESTS },
                        onAttendanceClick = { currentScreen = AppScreen.ATTENDANCE },
                        onDocumentsClick = { currentScreen = AppScreen.COMPANY_DOCUMENTS },
                        onProfileClick = { currentScreen = AppScreen.COMPANY_EDIT_PROFILE },
                        onNotificationsClick = { currentScreen = AppScreen.NOTIFICATIONS },
                        onLogout = {
                            currentUser = null
                            ApiClient.setToken(null)
                            NotificationApi.setToken(null)
                            onUserChanged(null)
                            currentScreen = AppScreen.LOGIN
                        }
                    )
                }
            }

            AppScreen.COMPANY_VACANCIES -> {
                CompanyVacanciesScreen(
                    token = ApiClient.getToken() ?: "",
                    onBack = { currentScreen = AppScreen.COMPANY_HOME }
                )
            }

            AppScreen.COMPANY_REQUESTS -> {
                CompanyRequestsScreen(
                    token = ApiClient.getToken() ?: "",
                    onBack = { currentScreen = AppScreen.COMPANY_HOME },
                    onStudentClick = { postulacion ->
                        selectedPostulacion = postulacion
                        currentScreen = AppScreen.STUDENT_PROFILE
                    }
                )
            }

            AppScreen.ATTENDANCE -> {
                AttendanceScreen(
                    token = ApiClient.getToken() ?: "",
                    onBack = { currentScreen = AppScreen.COMPANY_HOME }
                )
            }

            AppScreen.COMPANY_DOCUMENTS -> {
                CompanyDocumentsScreen(
                    token = ApiClient.getToken() ?: "",
                    onBack = { currentScreen = AppScreen.COMPANY_HOME }
                )
            }

            AppScreen.COMPANY_EDIT_PROFILE -> {
                CompanyEditProfileScreen(
                    token = ApiClient.getToken() ?: "",
                    onBack = { currentScreen = AppScreen.COMPANY_HOME },
                    onSaved = { currentScreen = AppScreen.COMPANY_HOME },
                    onLogout = {
                        currentUser = null
                        ApiClient.setToken(null)
                        NotificationApi.setToken(null)
                        onUserChanged(null)
                        currentScreen = AppScreen.LOGIN
                    }
                )
            }

            AppScreen.STUDENT_PROFILE -> {
                selectedPostulacion?.let { postulacion ->
                    StudentProfileScreen(
                        postulacion = postulacion,
                        onBack = { currentScreen = AppScreen.COMPANY_REQUESTS }
                    )
                }
            }

            // Admin screens
            AppScreen.ADMIN_HOME -> {
                currentUser?.let { user ->
                    AdminHomeScreen(
                        user = user,
                        token = ApiClient.getToken() ?: "",
                        onDocumentsClick = { currentScreen = AppScreen.ADMIN_DOCUMENTS },
                        onDeliveriesClick = { currentScreen = AppScreen.ADMIN_DELIVERIES },
                        onNotificationsClick = { currentScreen = AppScreen.NOTIFICATIONS },
                        onLogout = {
                            currentUser = null
                            ApiClient.setToken(null)
                            NotificationApi.setToken(null)
                            onUserChanged(null)
                            currentScreen = AppScreen.LOGIN
                        }
                    )
                }
            }

            AppScreen.ADMIN_DOCUMENTS -> {
                AdminDocumentsScreen(
                    token = ApiClient.getToken() ?: "",
                    onBack = { currentScreen = AppScreen.ADMIN_HOME }
                )
            }

            AppScreen.ADMIN_DELIVERIES -> {
                AdminDeliveriesScreen(
                    token = ApiClient.getToken() ?: "",
                    onBack = { currentScreen = AppScreen.ADMIN_HOME }
                )
            }

            AppScreen.NOTIFICATIONS -> {
                NotificationsScreen(
                    onBack = {
                        currentScreen = when (currentRole) {
                            UserRole.STUDENT -> AppScreen.STUDENT_HOME
                            UserRole.COMPANY -> AppScreen.COMPANY_HOME
                            UserRole.ADMIN -> AppScreen.ADMIN_HOME
                        }
                    }
                )
            }
        }
    }
}
