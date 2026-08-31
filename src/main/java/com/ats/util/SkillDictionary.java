package com.ats.util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Master dictionary of technical skills with synonyms and categories.
 * Used for skill matching and categorization in ATS scoring.
 */
public final class SkillDictionary {

    private SkillDictionary() {
        // Utility class
    }

    /**
     * Master list of all recognized skills (150+ skills).
     */
    public static final List<String> MASTER_SKILLS = List.of(
            // Programming Languages
            "Java", "Python", "JavaScript", "TypeScript", "C++", "C#", "Go", "Rust",
            "Kotlin", "Scala", "Ruby", "PHP", "Swift", "Dart", "R", "MATLAB", "Perl",
            "Groovy", "Lua", "Shell", "Bash", "PowerShell",

            // Backend Frameworks
            "Spring Boot", "Spring MVC", "Spring Security", "Spring Cloud", "Spring Data",
            "Spring Batch", "Hibernate", "JPA", "Node.js", "Express.js", "NestJS",
            "Django", "Flask", "FastAPI", "ASP.NET", ".NET Core", "Rails",
            "Micronaut", "Quarkus", "Vert.x",

            // Frontend Frameworks & Libraries
            "React", "Angular", "Vue.js", "Next.js", "Nuxt.js", "Svelte",
            "jQuery", "Redux", "MobX", "Tailwind CSS", "Bootstrap", "Material UI",
            "HTML", "CSS", "SASS", "LESS", "Webpack", "Vite",

            // Databases
            "MySQL", "PostgreSQL", "Oracle", "SQL Server", "MongoDB", "Cassandra",
            "Redis", "DynamoDB", "Elasticsearch", "Neo4j", "CouchDB", "MariaDB",
            "SQLite", "H2", "Memcached",

            // Cloud & DevOps
            "AWS", "Azure", "GCP", "Google Cloud", "Docker", "Kubernetes",
            "Terraform", "Ansible", "Jenkins", "GitHub Actions", "GitLab CI",
            "CircleCI", "Travis CI", "ArgoCD", "Helm",
            "AWS Lambda", "AWS S3", "AWS EC2", "AWS ECS", "AWS RDS",
            "Azure DevOps", "CloudFormation",

            // Messaging & Streaming
            "Kafka", "RabbitMQ", "ActiveMQ", "SQS", "SNS", "Apache Pulsar",
            "Apache Flink", "Apache Spark",

            // API & Protocols
            "REST API", "GraphQL", "gRPC", "WebSocket", "SOAP",
            "OpenAPI", "Swagger", "Postman",

            // Security
            "JWT", "OAuth", "OAuth2", "SAML", "LDAP", "Keycloak",
            "Spring Security", "SSL", "TLS",

            // Architecture
            "Microservices", "Monolithic", "Event Driven", "CQRS",
            "Domain Driven Design", "Serverless", "SOA",

            // Testing
            "JUnit", "Mockito", "TestNG", "Selenium", "Cypress", "Jest",
            "Mocha", "Pytest", "Cucumber", "JMeter", "Gatling",
            "SonarQube", "Jacoco",

            // Version Control & Tools
            "Git", "GitHub", "GitLab", "Bitbucket", "SVN",
            "Maven", "Gradle", "npm", "Yarn", "pip",

            // Monitoring & Logging
            "Prometheus", "Grafana", "ELK Stack", "Splunk", "Datadog",
            "New Relic", "Jaeger", "Zipkin", "Log4j", "SLF4J",

            // CI/CD
            "CI/CD", "Continuous Integration", "Continuous Deployment",
            "DevOps", "SRE", "Infrastructure as Code",

            // Data & ML
            "Machine Learning", "Deep Learning", "TensorFlow", "PyTorch",
            "Pandas", "NumPy", "Scikit-learn", "Hadoop", "Hive",
            "Apache Airflow", "ETL", "Data Pipeline",

            // Mobile
            "Android", "iOS", "React Native", "Flutter", "Xamarin",

            // Agile & Management
            "Agile", "Scrum", "Kanban", "JIRA", "Confluence",
            "Trello", "Slack"
    );

    /**
     * Synonym map for skill matching.
     * Key: normalized alternative → Value: canonical skill name
     */
    public static final Map<String, String> SYNONYMS;

    static {
        Map<String, String> synonyms = new HashMap<>();

        // REST API variants
        synonyms.put("restful api", "REST API");
        synonyms.put("restful apis", "REST API");
        synonyms.put("rest apis", "REST API");
        synonyms.put("restful", "REST API");
        synonyms.put("rest", "REST API");
        synonyms.put("restful services", "REST API");
        synonyms.put("rest services", "REST API");
        synonyms.put("restful web services", "REST API");

        // Spring Boot variants
        synonyms.put("springboot", "Spring Boot");
        synonyms.put("spring-boot", "Spring Boot");
        synonyms.put("spring framework", "Spring Boot");

        // Spring Security variants
        synonyms.put("springsecurity", "Spring Security");
        synonyms.put("spring-security", "Spring Security");

        // Spring Cloud variants
        synonyms.put("springcloud", "Spring Cloud");
        synonyms.put("spring-cloud", "Spring Cloud");

        // React variants
        synonyms.put("reactjs", "React");
        synonyms.put("react.js", "React");
        synonyms.put("react js", "React");

        // Angular variants
        synonyms.put("angularjs", "Angular");
        synonyms.put("angular.js", "Angular");
        synonyms.put("angular js", "Angular");

        // Vue variants
        synonyms.put("vuejs", "Vue.js");
        synonyms.put("vue", "Vue.js");

        // Node.js variants
        synonyms.put("nodejs", "Node.js");
        synonyms.put("node", "Node.js");

        // Express variants
        synonyms.put("expressjs", "Express.js");
        synonyms.put("express", "Express.js");

        // Docker variants
        synonyms.put("docker container", "Docker");
        synonyms.put("docker containers", "Docker");
        synonyms.put("containerization", "Docker");

        // Kubernetes variants
        synonyms.put("k8s", "Kubernetes");
        synonyms.put("kube", "Kubernetes");

        // AWS variants
        synonyms.put("amazon web services", "AWS");
        synonyms.put("amazon aws", "AWS");

        // Azure variants
        synonyms.put("microsoft azure", "Azure");
        synonyms.put("ms azure", "Azure");

        // GCP variants
        synonyms.put("google cloud platform", "GCP");
        synonyms.put("google cloud", "GCP");

        // MongoDB variants
        synonyms.put("mongo", "MongoDB");
        synonyms.put("mongo db", "MongoDB");

        // PostgreSQL variants
        synonyms.put("postgres", "PostgreSQL");
        synonyms.put("psql", "PostgreSQL");

        // Microservices variants
        synonyms.put("micro services", "Microservices");
        synonyms.put("micro-services", "Microservices");
        synonyms.put("microservice", "Microservices");
        synonyms.put("microservice architecture", "Microservices");

        // CI/CD variants
        synonyms.put("ci cd", "CI/CD");
        synonyms.put("cicd", "CI/CD");
        synonyms.put("ci-cd", "CI/CD");
        synonyms.put("continuous integration continuous deployment", "CI/CD");
        synonyms.put("continuous integration/continuous deployment", "CI/CD");

        // JWT variants
        synonyms.put("json web token", "JWT");
        synonyms.put("json web tokens", "JWT");

        // JPA/Hibernate variants
        synonyms.put("java persistence api", "JPA");
        synonyms.put("hibernate orm", "Hibernate");

        // JavaScript variants
        synonyms.put("js", "JavaScript");
        synonyms.put("ecmascript", "JavaScript");
        synonyms.put("es6", "JavaScript");
        synonyms.put("es2015", "JavaScript");

        // TypeScript variants
        synonyms.put("ts", "TypeScript");

        // Machine Learning variants
        synonyms.put("ml", "Machine Learning");
        synonyms.put("ai/ml", "Machine Learning");

        // Deep Learning variants
        synonyms.put("dl", "Deep Learning");

        // Elasticsearch variants
        synonyms.put("elastic search", "Elasticsearch");
        synonyms.put("elastic", "Elasticsearch");

        // GraphQL variants
        synonyms.put("graph ql", "GraphQL");

        // C# variants
        synonyms.put("csharp", "C#");
        synonyms.put("c sharp", "C#");

        // C++ variants
        synonyms.put("cpp", "C++");
        synonyms.put("cplusplus", "C++");

        SYNONYMS = Collections.unmodifiableMap(synonyms);
    }

    /**
     * Skill categories mapping.
     */
    public static final Map<String, List<String>> CATEGORIES;

    static {
        Map<String, List<String>> cats = new LinkedHashMap<>();

        cats.put("Languages", List.of(
                "Java", "Python", "JavaScript", "TypeScript", "C++", "C#", "Go", "Rust",
                "Kotlin", "Scala", "Ruby", "PHP", "Swift", "Dart", "R", "Perl", "Groovy"
        ));

        cats.put("Backend", List.of(
                "Spring Boot", "Spring MVC", "Spring Security", "Spring Cloud", "Spring Data",
                "Spring Batch", "Hibernate", "JPA", "Node.js", "Express.js", "NestJS",
                "Django", "Flask", "FastAPI", "ASP.NET", ".NET Core", "Rails",
                "Micronaut", "Quarkus"
        ));

        cats.put("Frontend", List.of(
                "React", "Angular", "Vue.js", "Next.js", "Svelte",
                "jQuery", "Redux", "Tailwind CSS", "Bootstrap", "Material UI",
                "HTML", "CSS", "SASS", "Webpack", "Vite"
        ));

        cats.put("Database", List.of(
                "MySQL", "PostgreSQL", "Oracle", "SQL Server", "MongoDB", "Cassandra",
                "Redis", "DynamoDB", "Elasticsearch", "Neo4j", "MariaDB", "SQLite", "H2", "Memcached"
        ));

        cats.put("Cloud", List.of(
                "AWS", "Azure", "GCP", "Google Cloud", "AWS Lambda", "AWS S3", "AWS EC2",
                "AWS ECS", "AWS RDS", "CloudFormation", "Serverless"
        ));

        cats.put("DevOps", List.of(
                "Docker", "Kubernetes", "Terraform", "Ansible", "Jenkins", "GitHub Actions",
                "GitLab CI", "CircleCI", "ArgoCD", "Helm", "CI/CD", "DevOps", "SRE"
        ));

        cats.put("Messaging", List.of(
                "Kafka", "RabbitMQ", "ActiveMQ", "SQS", "SNS", "Apache Pulsar"
        ));

        cats.put("Testing", List.of(
                "JUnit", "Mockito", "TestNG", "Selenium", "Cypress", "Jest",
                "Mocha", "Pytest", "Cucumber", "JMeter", "SonarQube"
        ));

        cats.put("Architecture", List.of(
                "Microservices", "Event Driven", "CQRS", "Domain Driven Design",
                "REST API", "GraphQL", "gRPC", "WebSocket", "SOAP"
        ));

        cats.put("Security", List.of(
                "JWT", "OAuth", "OAuth2", "SAML", "LDAP", "Keycloak", "SSL", "TLS"
        ));

        cats.put("Tools", List.of(
                "Git", "GitHub", "GitLab", "Maven", "Gradle", "npm", "JIRA",
                "Confluence", "Postman", "Swagger"
        ));

        CATEGORIES = Collections.unmodifiableMap(cats);
    }

    /**
     * Find the canonical skill name for a given text, checking exact match and synonyms.
     */
    public static String findSkill(String text) {
        if (text == null || text.isBlank()) return null;
        String normalized = text.trim().toLowerCase();

        // Exact match (case insensitive) against master list
        for (String skill : MASTER_SKILLS) {
            if (skill.toLowerCase().equals(normalized)) {
                return skill;
            }
        }

        // Synonym match
        String synonym = SYNONYMS.get(normalized);
        if (synonym != null) {
            return synonym;
        }

        return null;
    }

    /**
     * Get the category for a given skill.
     */
    public static String getCategory(String skill) {
        if (skill == null) return "Other";
        for (Map.Entry<String, List<String>> entry : CATEGORIES.entrySet()) {
            if (entry.getValue().stream().anyMatch(s -> s.equalsIgnoreCase(skill))) {
                return entry.getKey();
            }
        }
        return "Other";
    }

    /**
     * Extract all recognized skills from text.
     */
    public static List<String> extractSkills(String text) {
        if (text == null || text.isBlank()) return List.of();

        Set<String> found = new LinkedHashSet<>();
        String normalizedText = text.toLowerCase();

        // Check each master skill
        for (String skill : MASTER_SKILLS) {
            String skillLower = skill.toLowerCase();
            // Use word boundary matching for short skills
            if (skillLower.length() <= 3) {
                if (normalizedText.matches("(?s).*\\b" + java.util.regex.Pattern.quote(skillLower) + "\\b.*")) {
                    found.add(skill);
                }
            } else {
                if (normalizedText.contains(skillLower)) {
                    found.add(skill);
                }
            }
        }

        // Check synonyms
        for (Map.Entry<String, String> entry : SYNONYMS.entrySet()) {
            if (normalizedText.contains(entry.getKey())) {
                found.add(entry.getValue());
            }
        }

        return new ArrayList<>(found);
    }

    /**
     * Categorize a list of skills into category groups.
     */
    public static Map<String, List<String>> categorize(List<String> skills) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        for (String skill : skills) {
            String category = getCategory(skill);
            result.computeIfAbsent(category, k -> new ArrayList<>()).add(skill);
        }
        return result;
    }

    /**
     * Get important keywords for keyword density analysis.
     */
    public static List<String> getImportantKeywords() {
        return List.of(
                "spring", "react", "rest", "jwt", "docker", "kafka", "ci/cd",
                "microservices", "kubernetes", "aws", "cloud", "agile", "api",
                "database", "sql", "nosql", "security", "authentication",
                "authorization", "performance", "scalable", "distributed",
                "testing", "automation", "deploy", "monitor", "pipeline",
                "architecture", "design pattern", "data structure", "algorithm"
        );
    }
}
