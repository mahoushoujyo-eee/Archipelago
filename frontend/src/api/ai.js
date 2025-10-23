import axios from "axios";

const prefix = '/api/llm'

// 获取请求头，包含token
const getHeaders = () => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: token } : {}
}

export const aiApi = {
    getModels()
    {
        return axios.get(prefix + '/model/list', { headers: getHeaders() })
    },
    chat(params)
    {
        return axios.post(prefix + '/ai/chat', params, { headers: getHeaders() })
    },
    getConversationHistory(userId)
    {
        return axios.get(prefix + '/model/history/' + userId, { headers: getHeaders() })
    },
    deleteConversation(conversationId)
    {
        return axios.delete(prefix + '/model/history-conversation/' + conversationId, { headers: getHeaders() })   
    },
    getOneConversation(conversationId)
    {
        return axios.get(prefix + '/model/history-conversation/' + conversationId, { headers: getHeaders() })
    },
    updateConversation(params)
    {
        return axios.post(prefix + '/model/history-conversation/update', params, { headers: getHeaders() })
    },


    createKnowledge(params)
    {
        return axios.post(prefix + '/rag/create', params, { headers: getHeaders() })
    },
    getKnowledgeList(params)
    {
        return axios.post(prefix + '/rag/list', params, { headers: getHeaders() })
    },
    deleteKnowledge(params)
    {
        return axios.post(prefix + '/rag/delete', params, { headers: getHeaders() })
    },
    updateKnowledge(params)
    {
        return axios.post(prefix + '/rag/update', params, { headers: getHeaders() })
    },
    getUploadFileUrl(params)
    {
        return axios.post(prefix + '/rag/get-urls', params, { headers: getHeaders() })
    },
    loadFileIntoKnowledge(params)
    {
        return axios.post(prefix + '/rag/finish-upload', params, { headers: getHeaders() })
    }

}