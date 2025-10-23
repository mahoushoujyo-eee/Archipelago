import axios from "axios";

const prefix = 'api/resources'

// 获取请求头，包含token
const getHeaders = () => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: token } : {}
}

export const resourceApi = {
    getPublicResources(params)
    {
        return axios.post(`/${prefix}/oss/public/list`, params, { headers: getHeaders() })
    },
    getDownloadUrl(params)
    {
        return axios.post(`/${prefix}/oss/public/download/url`, params, { headers: getHeaders() })
    },
    getPrivateResource(params)
    {
        return axios.post(`/${prefix}/oss/private/list`, params, { headers: getHeaders() })
    },
    getPublicCount()
    {
        return axios.get(`/${prefix}/oss/public/count`, { headers: getHeaders() })
    },
    getPrivateCount(params)
    {
        return axios.post(`/${prefix}/oss/private/count`, params, { headers: getHeaders() })
    },
    deleteResource(params)
    {
        return axios.post(`/${prefix}/oss/delete`, params, { headers: getHeaders() })
    },
    getOssSignature(params)
    {
        return axios.post(`/${prefix}/oss/add`, params, { headers: getHeaders() })
    },
    getPrivateUsage(params)
    {
        return axios.post(`/${prefix}/oss/private/usage`, params, { headers: getHeaders() })
    }
}