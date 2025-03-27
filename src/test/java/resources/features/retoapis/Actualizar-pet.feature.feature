Feature: Reto Técnico BackEnd

  Background:
    * url urlBase
    * def ResponseAgregarNuevaMascota = call read('Create-pet.feature.feature@AgregarNuevaMascota')
    * def idMascota = ResponseAgregarNuevaMascota.idMascota

  @ActualizarMascota @ActualizarMascotaScenario01
  Scenario: Actualizar el nombre de la mascota usando PUT
    * def RequestActualizarMascota = read('classpath:resources/request/RequestActualizarMascota.json')
    Given path 'pet'
    And request RequestActualizarMascota
    When method put
    Then status 200

  @ActualizarMascota @ActualizarMascotaScenario02
  Scenario: Actualizar mascota con ID inválido (status 500)
    * def mascotaIdInvalido =
      """
      {
        "id": "abc",
        "category": { "id": 1, "name": "perros" },
        "name": "InvalidIdDog",
        "photoUrls": ["https://example.com"],
        "tags": [{ "id": 1, "name": "test" }],
        "status": "available"
      }
      """
    Given path 'pet'
    And request mascotaIdInvalido
    When method put
    Then status 500


