package com.tegar.istoriya.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.tegar.istoriya.data.api.ResultState
import com.tegar.istoriya.data.api.response.LoginResponse
import com.tegar.istoriya.data.api.response.RegisterResponse
import com.tegar.istoriya.data.pref.UserModel
import com.tegar.istoriya.data.repository.UserRepository
import com.tegar.istoriya.utils.DataDummy
import com.tegar.istoriya.utils.MainDispatcherRule
import com.tegar.istoriya.utils.getOrAwaitValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)

class AuthViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var authRepository: UserRepository


    private lateinit var authViewModel: AuthViewModel

    @Before
    fun setup(){
        authViewModel = AuthViewModel(authRepository)
    }
    /*
        A : Testing Login
        B : Testing Register

     */
    /*
        Scenario  A1 : Testing login successfully then return response data not null and Result state response
     */
    @Test
    fun `Login successfully - result success`() = runTest {
        val expectedResponse = MutableLiveData<ResultState<LoginResponse>>()
        expectedResponse.value = ResultState.Success(dummyLoginResponse)

        `when`(authRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = authViewModel.login(dummyEmail,dummyPassword).getOrAwaitValue()

        Mockito.verify(authRepository).login(dummyEmail,dummyPassword)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResultState.Success)

    }

    /*
        Scenario A2 : Testing for error message is showing or not
     */
    @Test
    fun `when Login failed must return error message `() = runTest {
        val expectedResponse = MutableLiveData<ResultState<LoginResponse>>()
        val errorMessage = "Invalid credentials"

        expectedResponse.value = ResultState.Error(errorMessage)

        `when`(authRepository.login(dummyEmail, dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = authViewModel.login(dummyEmail,dummyPassword).getOrAwaitValue()

        Mockito.verify(authRepository).login(dummyEmail,dummyPassword)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResultState.Error)
        assertEquals(errorMessage, (actualResponse as ResultState.Error).error) // Check is Resultstate.error return error message or not

    }




    /*
        Scenario B1 :  Register successfully then must return response and message User created
     */
    @Test
    fun `when user register successfully `() = runTest {
        val expectedResponse = MutableLiveData<ResultState<RegisterResponse>>()

        expectedResponse.value = ResultState.Success(dummyRegisterResponse)
        `when`(authRepository.register(dummyName,dummyEmail, dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = authViewModel.register(dummyName,dummyEmail,dummyPassword).getOrAwaitValue()

        Mockito.verify(authRepository).register(dummyName,dummyEmail,dummyPassword)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResultState.Success)
        assertEquals(false, (actualResponse as ResultState.Success).data.error)
        assertEquals("User created", (actualResponse as ResultState.Success).data.message)

    }
    /*
        Scenario B2 :  Register Failed then must return message
     */
    @Test
    fun `when user register must return error message `() = runTest {
        val expectedResponse = MutableLiveData<ResultState<RegisterResponse>>()
        val errorMessage = "Unexpected Error"

        expectedResponse.value = ResultState.Error(errorMessage)

        `when`(authRepository.register(dummyName,dummyEmail, dummyPassword)).thenReturn(expectedResponse)
        val actualResponse = authViewModel.register(dummyName,dummyEmail,dummyPassword).getOrAwaitValue()

        Mockito.verify(authRepository).register(dummyName,dummyEmail,dummyPassword)
        assertNotNull(actualResponse)
        assertTrue(actualResponse is ResultState.Error)
        assertEquals(errorMessage, (actualResponse as ResultState.Error).error)

    }
@Test
    fun`getSession return must user data`() =  runBlocking {
        val dummyUserModel =  UserModel(
            dummyUserId,
            dummyName,
            dummyToken
        )
        val expectedResponse =  MutableStateFlow<UserModel>(dummyUserModel)
        `when`(authRepository.getSession()).thenReturn(expectedResponse)
        val actualResponse = authViewModel.getSession().getOrAwaitValue()
        Mockito.verify(authRepository).getSession()
        assertNotNull(actualResponse)
        assertEquals(dummyUserModel, actualResponse)


    }

    @Test
    fun `logout calls repository's logout`() = runBlocking {
        authViewModel.logout()
        Mockito.verify(authRepository).logout()
    }



    companion object{
        private val dummyToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXViUE5nOVpVaDFNMnhPeHgiLCJpYXQiOjE2OTgwMzA5Mjd9.pGe0Vv-C5FpBQlG8iIzzVeFuTPBkga4g9ZOnyvBlmd0"
        private var dummyUserId = "user-ubPNg9ZUh1M2xOxx"
        private var dummyName = "tegar"
        private var dummyEmail = "email@mail.com"
        private val dummyPassword = "password"

        private val dummyLoginResponse =  DataDummy.generateDummyLoginResponse()
        private val dummyRegisterResponse = DataDummy.generaRegisterDummyResponse()
    }


}