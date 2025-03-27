Feature: Reto Técnico BackEnd

  Background:
    * url urlBase

  @AgregarNuevaMascota @NuevaMascota01
  Scenario: Agregar una nueva mascota usando POST
    * def RequestNuevaMascota = read('classpath:resources/request/RequestNuevaMascota.json')
    * def ResponseNuevaMascota = read('classpath:resources/response/ResponseNuevaMascota.json')
    Given path '/pet'
    And request RequestNuevaMascota
    When method post
    Then status 200
    And match response == ResponseNuevaMascota
    * def idMascota = response.id



  @AgregarNuevaMascota @NuevaMascota02
  Scenario: Agregar mascota con input inválido status 415 Json Vacio
    * def mascotaInvalida =
    """
    """
    Given path 'pet'
    And request mascotaInvalida
    When method post
    And status 415

