package com.example.jjmfronted

import androidx.compose.runtime.*
import com.example.jjmfronted.models.User
import com.example.jjmfronted.models.UserRole
import com.example.jjmfronted.network.AuthApi
import com.example.jjmfronted.screens.*
import com.example.jjmfronted.screens.admin.AdminDeliveriesScreen
import com.example.jjmfronted.screens.admin.AdminDocumentsScreen
import com.example.jjmfronted.screens.admin.AdminHomeScreen
import com.example.jjmfronted.screens.auth.ForgotPasswordScreen
import com.example.jjmfronted.screens.auth.LoginScreen
import com.example.jjmfronted.screens.auth.RegisterScreen
import com.example.jjmfronted.screens.company.*
import com.example.jjmfronted.screens.student.*
import com.example.jjmfronted.ui.theme.JJMTheme
import com.example.jjmfronted.models.MockVacancy
import kotlinx.coroutines.launch

enum class AppScreen {
    LOGIN, REGISTER, FORGOT_PASSWORD,
    STUDENT_HOME, VACANCY_DETAIL, APPLICATIONS, STUDENT_DOCUMENTS, MAP, PROFILE,
    COMPANY_HOME, COMPANY_VACANCIES, COMPANY_REQUESTS, ATTENDANCE, COMPANY_DOCUMENTS,
    ADMIN_HOME, ADMIN_DOCUMENTS, ADMIN_DELIVERIES
}

@Composable
fun App() {
    JJMTheme {
        var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }
        var currentUser by remember { mutableStateOf<User?>(null) }
        var currentRole by remember { mutableStateOf(UserRole.STUDENT) }
        var isLoading by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf<String?>(null) }
        var selectedVacancy by remember { mutableStateOf<MockVacancy?>(null) }
        val scope = rememberCoroutineScope()

        when (currentScreen) {
            AppScreen.LOGIN -> {
                LoginScreen(
                    onLogin = { email, password ->
                        scope.launch {
                            isLoading = true
                            error = null
                            val result = AuthApi.login(email, password)
                            result.fold(
                                onSuccess = { response ->
                                    currentUser = response.user
                                    currentRole = UserRole.STUDENT
                                    currentScreen = AppScreen.STUDENT_HOME
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
                            val result = AuthApi.register(email, password, name)
                            result.fold(
                                onSuccess = { response ->
                                    currentUser = response.user
                                    currentRole = role
                                    currentScreen = when (role) {
                                        UserRole.STUDENT -> AppScreen.STUDENT_HOME
                                        UserRole.COMPANY -> AppScreen.COMPANY_HOME
                                        UserRole.ADMIN -> AppScreen.ADMIN_HOME
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
                        onVacancyClick = { vacancy ->
                            selectedVacancy = vacancy
                            currentScreen = AppScreen.VACANCY_DETAIL
                        },
                        onApplicationsClick = { currentScreen = AppScreen.APPLICATIONS },
                        onDocumentsClick = { currentScreen = AppScreen.STUDENT_DOCUMENTS },
                        onMapClick = { currentScreen = AppScreen.MAP },
                        onProfileClick = { currentScreen = AppScreen.PROFILE },
                        onLogout = {
                            currentUser = null
                            currentScreen = AppScreen.LOGIN
                        }
                    )
                }
            }

            AppScreen.VACANCY_DETAIL -> {
                selectedVacancy?.let { vacancy ->
                    VacancyDetailScreen(
                        vacancy = vacancy,
                        onBack = { currentScreen = AppScreen.STUDENT_HOME },
                        onApply = { currentScreen = AppScreen.APPLICATIONS }
                    )
                }
            }

            AppScreen.APPLICATIONS -> {
                ApplicationStatusScreen(onBack = { currentScreen = AppScreen.STUDENT_HOME })
            }

            AppScreen.STUDENT_DOCUMENTS -> {
                StudentDocumentsScreen(onBack = { currentScreen = AppScreen.STUDENT_HOME })
            }

            AppScreen.MAP -> {
                MapScreen(onBack = { currentScreen = AppScreen.STUDENT_HOME })
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
                            currentScreen = AppScreen.LOGIN
                        }
                    )
                }
            }

            // Company screens
            AppScreen.COMPANY_HOME -> {
                CompanyHomeScreen(
                    onVacanciesClick = { currentScreen = AppScreen.COMPANY_VACANCIES },
                    onRequestsClick = { currentScreen = AppScreen.COMPANY_REQUESTS },
                    onAttendanceClick = { currentScreen = AppScreen.ATTENDANCE },
                    onDocumentsClick = { currentScreen = AppScreen.COMPANY_DOCUMENTS },
                    onProfileClick = { currentScreen = AppScreen.PROFILE },
                    onLogout = {
                        currentUser = null
                        currentScreen = AppScreen.LOGIN
                    }
                )
            }

            AppScreen.COMPANY_VACANCIES -> {
                CompanyVacanciesScreen(onBack = { currentScreen = AppScreen.COMPANY_HOME })
            }

            AppScreen.COMPANY_REQUESTS -> {
                CompanyRequestsScreen(onBack = { currentScreen = AppScreen.COMPANY_HOME })
            }

            AppScreen.ATTENDANCE -> {
                AttendanceScreen(onBack = { currentScreen = AppScreen.COMPANY_HOME })
            }

            AppScreen.COMPANY_DOCUMENTS -> {
                CompanyDocumentsScreen(onBack = { currentScreen = AppScreen.COMPANY_HOME })
            }

            // Admin screens
            AppScreen.ADMIN_HOME -> {
                AdminHomeScreen(
                    onDocumentsClick = { currentScreen = AppScreen.ADMIN_DOCUMENTS },
                    onDeliveriesClick = { currentScreen = AppScreen.ADMIN_DELIVERIES },
                    onLogout = {
                        currentUser = null
                        currentScreen = AppScreen.LOGIN
                    }
                )
            }

            AppScreen.ADMIN_DOCUMENTS -> {
                AdminDocumentsScreen(onBack = { currentScreen = AppScreen.ADMIN_HOME })
            }

            AppScreen.ADMIN_DELIVERIES -> {
                AdminDeliveriesScreen(onBack = { currentScreen = AppScreen.ADMIN_HOME })
            }
        }
    }
}
