# POD-TP2-G5
Diseñar e implementar una aplicación de consola que utilice el modelo de programación
MapReduce junto con el framework Hazelcast para el procesamiento de multas de
estacionamiento, basado en datos reales.
Para este trabajo se busca poder procesar datos de multas de estacionamiento de las
ciudades de Nueva York, EEUU y Chicago, EEUU.

# Requisitos
* Java >= 20.0.0
* Apache Maven >= 3.9.4.
* Hazelcast = 3.8.6

# Instalación

Compilar el programa con el siguiente comando maven:

```bash
mvn clean install
```

# Funcionamiento General

## Instrucciones para la descarga de archivos
Para la ejecución de la aplicación, se deben ejecutar los siguientes comandos en la carpeta raíz del proyecto y así obtener los archivos necesarios:

Si se encuentra en pampero:
```bash
cp /afs/it.itba.edu.ar/pub/pod/agenciesNYC.csv .
cp /afs/it.itba.edu.ar/pub/pod/infractionsNYC.csv .
cp /afs/it.itba.edu.ar/pub/pod/ticketsNYC.csv .
```

Si se encuentra en otro servidor:
```bash
scp [usuarioPampero]@pampero.itba.edu.ar:/afs/it.itba.edu.ar/pub/pod/agenciesNYC.csv .
scp [usuarioPampero]@pampero.itba.edu.ar:/afs/it.itba.edu.ar/pub/pod/infractionsNYC.csv .
scp dlimachi@pampero.itba.edu.ar:/afs/it.itba.edu.ar/pub/pod/ticketsNYC.csv .
```

## Ejecución del Servidor
Para ejecutar el servidor, correr el script run-server con el siguiente comando:

```bash
sh server/src/main/assembly/overlay/run-server.sh -Daddress**  #Network Interface Address
```

## Ejecución del Cliente

Para ejecutar el cliente, correr el script de alguna queryX con el siguiente comando:

```bash
sh client/src/main/assembly/overlay/queryX.sh #[opciones]
```

### **[opciones]**:
* **-Daddresses** = Direcciones de los nodos separadas por punto y coma.
* **-DinPath** = Path a la carpeta que contiene los archivos CSV de entrada.
* **-DoutPath** = Path a la carpeta donde se guardarán los archivos de salida queryN.csv y timeN.txt.
* **-Dcity** = Ciudad de la que se quieren obtener los datos.
* **-Dn** = Límite para la query 3 y 4.
* **-Dagency** = Agencia seleccionada para la query 4. [pe: DEPARTMENT_OF_TRANSPORTATION]
* **-Dfrom** = Fecha de inicio para la query 5. [pe: '03/12/2013']
* **-Dto** = Fecha de fin para la query 5. [pe: '03/12/2013']

