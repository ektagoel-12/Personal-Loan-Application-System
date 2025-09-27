import axios from "axios"

const base_url = "http://localhost:8080"

export async function makeRequestWithoutToken(type, endpoint, body) {
    const url = base_url + endpoint;
    let response = null;
    try{
        switch (type) {
            case "GET":
                response = await axios.get(url);
                break;
            case "POST":
                response = await axios.post(url, body);
                break;
            case "PATCH":
                response = await axios.patch(url, body);
                break;
            case "DELETE":
                response = await axios.delete(url);
                break;
            default:
                throw new Error(`Unsupported request type: ${type}`);
        }
    }
    catch(error){
        alert(error?.response?.data?.error || "some error occured")
    }

    return response;
}

export async function makeRequestWithToken(type, endpoint, body) {
    const url = base_url + endpoint;
    const token = localStorage.getItem("token");
    let response = null;

    const config = {
        headers: { Authorization: `Bearer ${token}` }
    };

    try{
        switch (type) {
            case "GET":
                response = await axios.get(url, config);
                break;
            case "POST":
                response = await axios.post(url, body, config);
                break;
            case "PUT":
                response = await axios.put(url, body, config);
                break;
            case "PATCH":
                response = await axios.patch(url, body, config);
                break;
            case "DELETE":
                response = await axios.delete(url, config);
                break;
            default:
                throw new Error(`Unsupported request type: ${type}`);
        }
        return response;
    }
    catch(error){
        if(error?.response?.data?.error === "The token is expired"){
            const refreshToken = localStorage.getItem("refreshToken");
            try {
            const refreshResponse = await axios.post(`${base_url}/auth/refresh`, { refreshToken });

            const { accessToken, refreshToken: newRefreshToken } = refreshResponse.data;

            localStorage.setItem("token", accessToken);
            localStorage.setItem("refreshToken", newRefreshToken);

            const response = await makeRequestWithToken(type, endpoint, body);
            return response;
            } catch (error) {
                localStorage.removeItem("token");
                localStorage.removeItem("refreshToken");
                localStorage.removeItem("currUser")
                window.location.href = "/login-form";  // Redirect to login page
            }
        }
    }

    return response;
}
