package io.github.khoben.arpermission.exception

class IllegalRegisterEntity :
    IllegalArgumentException("Only Fragment or AppCompatActivity can register permission request")