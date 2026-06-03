import { defineStore } from 'pinia'
import { ref } from 'vue'
import apiClient from '../api/client'
import dayjs from 'dayjs';
export const useCustomerStore = defineStore('customer', () => {
    const customers = ref([]);
    const getCustomers = async () => {
        try {
            const response = await apiClient.get('/user');
            customers.value = response.data.result;
        } catch (error) {
            console.error('Lỗi khi gọi API:', error)
        }
    }

    const createCustomer = async (data) => {
        try {
            const formData = new FormData();
            formData.append('username', data.username);
            formData.append('password', data.password);
            formData.append('firstName', data.firstName);
            formData.append('lastName', data.lastName);
            formData.append('email', data.email);
            formData.append('phoneNumber', data.phoneNumber);
            formData.append('gender', data.gender);
            formData.append('dateOrBirth', dayjs(data.dateOrBirth).format('MM-DD-YYYY'));
            formData.append('national', data.national);
            formData.append('idCard', data.idCard);
            console.log("formdata: ", formData);
            for (let [key, value] of formData.entries()) {
                console.log(key, value);
            }
            const response = await apiClient.post('/user', formData, {
                headers: { 'Content-Type': 'multipart/form-data' }
            });
            console.log("response123: ", response);
            customers.value.push(response.data.result);
            return { success: true, code: response.data.code }

        } catch (error) {
            console.error('Lỗi createCustomer:', error.response?.data || error.message)
            return { success: false, message: error.response?.data?.message || 'Lỗi không xác định' }
        }
    }

    return {
        customers,
        getCustomers,
        createCustomer
    }
})