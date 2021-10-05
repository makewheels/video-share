var loginToken = localStorage.getItem("loginToken");
axios.interceptors.request.use(function (config) {
    config.headers.loginToken = loginToken;
    return config;
});

// var BASEURL = "http://localhost";
var BASEURL = "http://java8.icu";
var APIURL = BASEURL + ":5018";
