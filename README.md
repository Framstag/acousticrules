# AcousticRules

## Attention

Some of the mechanism likely have to get adapted in the future, since SonarQube
deprecated some of the feature we are currently rely on.

See [Documentation regarding issues and their attributes](https://docs.sonarsource.com/sonarqube/latest/user-guide/issues/). Note especially deprecations regarding

- issue types
- issue severity
- Categorisation regarding [Clean Code](https://docs.sonarsource.com/sonarqube/latest/user-guide/clean-code/)

I also see, that there are some rules that have no tags, while may make them fall through the selection process, too.

Since the new clean code based attributes are also available in the downloaded
rule definition data, we can extend AcousticRules to have such information also available and add additional filter criteria to group rules based on this information, too. 

## What is AcousticRules?

It a simple application that works on an exported sonar ruleset
for a programming language (for example C++ or Java) by using a
simple  JSON-based DSL to define groups of rules by selection and filtering,
allows modification of these groups of rules or individual rules in a group.
From these modified rules it then generates a QualityProfile.xml than can get
imported into Sonar.

The idea behind this is, that you do not manually manage your rules, especially
their severity. You want to assign your personal Severity group
wise ("all Bugs are BLOCKER") with possibly only a few exclusions
(...but not this one). AcousticRules allows you to do this, especially
if Sonar implements new rules or deprecates old rules you just regenerate
and reimport the QualityProfile. It also allows you to generate
variants of profiles in an easy way.

This may be useful, in cases, where you do not want to use the default severity
of Sonar, you do not have a company-wide Sonar quality profile but want to 
tailor the profile for your application or want to produce a family of 
similar quality profiles with little variance.

AcousticRules may be helpful in situations where severity depends on the ranking of quality
features of your software, especially security, maintainability or compatibility
to some standard may be ranked differently from project to project.

It may also be helpful, if you have a legacy application where you want to
start with a small set of rules and later on bulk add groups of rules
from time to time as quality improves.

AcousticRules also generates markdown files for documentation
while at it, so you do ot only have a QualityProfile but a documentation
why the severity of a rule is the way it is.

## Other Reasons

AcousticRules is also a test application for me in my search to 
generate simple DSLs for simple data modifications and
transformations without writing a custom Scanner/Parser or a 
Parser generator tool.

It teaches me how in this case to build JSON based DSLs
(with their advantages and disadvantages).

## About the Name

I try to avoid product names in the name of my projects but try to find
a useful association....and in the end, a name is just a unique id.

## License

The application and its data files are under Apache License 2.0.

## The Idea behind AcousticRules

The idea of AcousticRule sis, the grouping of Sonar rules into disjunctive 
groups, where each group has a clear topic. Grouping is done based
on the different information available on rules.

After separating the rules into different groups you can easily manipulate
rules with in a group together. Examples are 

- Disabling
- Increasing or decreasing severity

Individual rule manipulation of course is still possible.

Finally, a rich documentation is created state in which group a rule is,
why it is in group. It also documents manipulation of rules together
with the reason for manipulation.

It is suggested to start with clarifying the ranking quality requirements
on your project and this manipulate the severity of rules accordingly.

AcousticRules already comes with a rich et of group definitions, however
you can write your own groups.

## How does it work?

AcousticRules internal mechanic implements the following steps:

1. Loading of the passed rule export files.
2. Loading of the processing group definitions (in the standard directory layout (the rules/*.json files)
3. Executing the processing group definitions resulting in a list of rules for each group
4. (Optional) Check for rules being in multiple rules
5. (Optional) Loading of the passed QualityProfile
6. (Optional) Executing the Quality Profile, resulting in modified group sof rules
7. (Optional) Generation of the Sonar Quality Profile file (*.xml)
8. (Optional) Generation of the QualityProfile documentation (*.md))

The execution of the processing group definition consists of the following (sub-) steps:

1. Execution of the list of selectors creating a list of rules
2. Execution of the list of filters on this list of rules, resulting in a possibly reduced list of rules

The execution of the QualityProfile group definition consists of the following (sub-) steps:

1. Execution of the list of filters, further reducing the list of rules
2. Execution of the list of modifiers on the list of rules returning the same list, but with potentially modified rules

## Selectors, Filters and Modifier

### Selectors

| Name           | Parameter                |
|----------------|--------------------------|
| SelectWithKey  | "keys": Array of String  | 
| SelectWithTag  | "tags": Array of String  |
| SelectWithType | "types": Array of String | 

### Filters

| Name              | Parameter                |
|-------------------|--------------------------|
| DropWithKey       | "keys": Array of String  |
| DropWithTag       | "tags": Array of String  |
| DropWithType      | "types": Array of String |
| DropNotWithType   | "types": Array of String |
| RemoveDeprecated  |                          |

### Modifier

| Name           | Parameter                                       |
|----------------|-------------------------------------------------|
| ChangeSeverity | "from": String, "to": String                    |
| DisableByKey   | "keys": Array of String                         |
| SetParamForKey | "key": String, "param": String, "value": String |
| SetSeverity    | "keys": Array of String, "to": String           |

### Dropping vs. Disabling

Dropped rules are remove from the internal lists and thus will not occur
in a documentation (yu also will not se the reason for dropping there). 

Disabled rules will stay in the list but will be removed from the 
generated QualityProfile. They will however appear in the documentation
and thus will mention the reason for disabling.

Recommendation: Do not remove rules in the QualityProfile definition, 
just disabled them since you will get a better documentation.

## Roll your own vs. pull requests...

There is no need to use the rule sin the `rules` subdirectory.
You can always roll your own rules, diving the rules into
groups by your own criteria.

However, we are interested to further enhance the quality of groups,
possibly creating more fine granular groups, allowing even better
fine-tuning of the QualityProfile.

Especially better separation of toolset, compilers, environments
would be helpful.

So, patches are welcome :-)

## Downloading of Rules from a Sonar Server

For AcousticRules you need a list of Rules for a programming
language. You can download such a list via the REST API and for example
curl.

See the following example for the Sonarcloud instance:

```bash
curl -v -u <user_token>: -o rules1.json "https://sonarcloud.io/api/rules/search?organization=<organisation>&languages=<language>&ps=500&p=<page>"
```

where:

| Placeholder  | Meaning                                                        |
|--------------|----------------------------------------------------------------|
| user_token   | **Sonar** user token for authentication                        |
| organisation | Name of the organisation the user belongs to (e.g. `framstag`) |
 | language     | name of the language, see below                                |
| page         | Number of page                                                 |

Note that Sonar uses paging, so ou will not get all rules in one go but must 
traverse pages by maximum 500 rules.

Depending on your installation, filtering by organization is not supported or
at least not required.

Names for languages:

| Language   | Name |
|------------|------|
| Java       | java |
| C++        | cpp  |
| TypeScript | ts   |
| HTML       | web  | 
| CSS        | css  |

## Commandline Options

Commandline options are still quickly changing, so I suggest to simply
call AcousticRules --help.

The current command lines for generating a C++ quality profile are:

```bash
--stopOnDuplicates @rules_import.options -q CPP_QualityProfile.json cpp_rules1.json cpp_rules2.json
```

where `cpp_rules1.json` and `cpp_rules2.json` are the C++ rules downloaded
via the Sonar REST API.

## Error Handling

Currently, there is none. I plan to either do schema validation or use
Bean Validation API in the context of JSON data loading to add simple
input validation.

Up to that time you will get Null-Pointer Exception and low level JSON
deserialization errors.

## How to Build

AcousticRules needs Java 17 or higher to build as it uses some
feature of this version.

The build creates a `*.jar` and an "all-in-one" jar for execution
and distribution.

If GraalVM is installed you can create a native executable by setting
the `native` profile (`mvn -pnative package')