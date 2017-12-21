package bcs.testing.test.REST.Error

class ErrorResponse constructor(private val status: Int, private val code: Int, private val message: String?) {
    fun getStatus(): Int {
        return this.status
    }

    fun getCode(): Int {
        return this.code
    }

    fun getMessage(): String? {
        return this.message
    }

    override fun toString(): String {
        return "ErrorResponse{status=${this.status}, code=${this.code}, message=${this.message}}"
    }

}