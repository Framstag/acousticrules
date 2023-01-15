# AcousticRules

## What ist AcousticRules?

It a simple application that works on an exported sonar ruleset
for a programming lanuage (for example C++ or Java) by using a
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

It may be helpful where severity depends and the ranking of quality
features of your software, especially security, maintainability or compatibility
to some standard may be ranked differently from project to project.

It may also be helpful, if you have a legacy application where you want to
start with a small set of rules and later on bulk add groups of rules
from time to time as quality improves.

AcousticRules also generates markdown files for documentation
while at it, so you do ot only have a QualityProfile but a documentation
why the severity of a rule is the way it is.

## Other reasons

AcousticRules is also a test application for me in my search to 
generate simple DSLs for simple data modifications and
transformations without writing a custom Scanner/Parser or a 
Parser generator tool.

It teaches me how in this case to build JSON based DSLs
(with their advantages and disandvantages).

## About the name

I try to avoid product names in the name of my projects but try to find
a useful association....and in the end, a name is just a unique id.

## License

The application and its data files are under Apache License 2.0.

## Downloading of rules from a sonar server

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
| user_token   | Sonar user token for authentification                          |
| organisation | Name of the organisation the user belongs to (e.g. `framstag`) |
 | language     | name of the language, see below                                |
| page         | Number of page                                                 |

Note that Sonar uses paging, so ou will not get all rules in one go but must 
traverse pages by maximum 500 rules.

Depending on your installation, filtering by organization is not supported or
at least not required.

Names for languages:

| Language | Name |
|----------|------|
| Java     | java |
| C++      | cpp  |

## Commandline options

Commandline options are still quickly changing, so I suggest to simply
call AcousticRules --help.

The current command lines for generating a C++ quality profile are:

```bash
--stopOnDuplicates @rules_import.options -q CPP_QualityProfile.json cpp_rules1.json cpp_rules2.json
```

where `cpp_rules1.json` and `cpp_rules2.json` are the C++ rules downloaded
via the Sonar REST API.

## Error handling

Currently, there is none. I plan to either do schema validation or use
Bean Validation API in the context of JSON data loading to add simple
input validation.

Up to that time you will get Null-Pointer Exception and low level JSON
deserialisation errors.