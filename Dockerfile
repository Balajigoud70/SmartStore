# 1. Java 11 వాడుతున్నాం కాబట్టి, దానికి సరిపోయే బేస్ ఇమేజ్
FROM openjdk:11-jdk-slim

# 2. 'target' ఫోల్డర్ లో బిల్డ్ అయిన jar ఫైల్ ని కంటైనర్ లోకి కాపీ చేస్తుంది
# ఫైల్ పేరు కచ్చితంగా మీ target ఫోల్డర్ లో ఉన్న పేరుతోనే ఉండాలి
COPY target/SmartStore-Backend-0.0.1-SNAPSHOT.jar app.jar

# 3. కంటైనర్ స్టార్ట్ అయినప్పుడు ఈ కమాండ్ రన్ అవుతుంది
ENTRYPOINT ["java","-jar","/app.jar"]