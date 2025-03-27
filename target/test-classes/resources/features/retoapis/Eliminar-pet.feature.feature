Feature: Reto Técnico BackEnd

  Background:
    * url urlBase
    * def ResponseAgregarNuevaMascota = call read('Create-pet.feature.feature@AgregarNuevaMascota')
    * def idMascota = ResponseAgregarNuevaMascota.idMascota

  @EliminarMascota @EliminarMascotaScenario01
  Scenario: Eliminar la mascota usando DELETE
    Given path 'pet', idMascota
    When method delete
    Then status 200

  @EliminarMascota @EliminarMascotaScenario02
  Scenario: Eliminar mascota con ID inválido (status 400)
    * def invalidPetId = 'abc'
    Given path 'pet', invalidPetId
    When method delete
    Then status 404
