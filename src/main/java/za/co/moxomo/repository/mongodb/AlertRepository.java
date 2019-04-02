package za.co.moxomo.repository.mongodb;

import org.springframework.data.repository.CrudRepository;

import za.co.moxomo.domain.Alert;

public interface AlertRepository  extends CrudRepository<Alert, String>{

}
