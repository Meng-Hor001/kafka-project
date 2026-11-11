# របៀបដែល Kafka Producer ជ្រើសរើស Partition ដោយប្រើ Message Key

នៅក្នុង Project របស់អ្នក Kafka Producer ជ្រើសរើស Partition ដោយផ្អែកលើ Message Key បន្ទាប់មកផ្ញើ Message ទៅ Kafka ដើម្បីរក្សាទុក។

Topic របស់អ្នកមាន 3 Partitions គឺ `0`, `1` និង `2`។

## 1. Client ផ្ញើទិន្នន័យ (Client Sends Data)

ឧទាហរណ៍ Client ផ្ញើ Request តាមរយៈ Postman៖

```
{
  "key": "order-001",
  "message": "Order created"
}
```

Application របស់អ្នកនឹងបញ្ជូនទិន្នន័យទាំងនេះតាមលំដាប់៖

```
MessageController
       ↓
MessageService
       ↓
KafkaMessagePublisher
```

បន្ទាប់មក `KafkaMessagePublisher` ហៅ Method៖

```
kafkaTemplate.send(topic, key, message);
```

ក្នុងនោះ៖

```
topic   = "demo-topic"
key     = "order-001"
message = "Order created"
```

ការពន្យល់៖

- `topic`៖ ឈ្មោះ Kafka Topic ដែលត្រូវផ្ញើ Message ទៅ។
- `key`៖ តម្លៃដែល Kafka Producer ប្រើដើម្បីជ្រើសរើស Partition។
- `message`៖ ទិន្នន័យដែលត្រូវផ្ញើ និងរក្សាទុកក្នុង Kafka។

## 2. Producer បម្លែង Key និង Value ទៅជា Bytes

មុនពេលផ្ញើ Message ទៅ Kafka, Producer ត្រូវបម្លែង Key និង Value ទៅជា Bytes ជាមុនសិន។

ក្នុង Project របស់អ្នក `StringSerializer` ត្រូវបានប្រើដើម្បីបម្លែង String ទៅជា Bytes ដែល Kafka អាចបញ្ជូន និងរក្សាទុកបាន។

ឧទាហរណ៍៖

```
"order-001"     → Key Bytes
"Order created" → Value Bytes
```

ការពន្យល់៖

Kafka មិនផ្ញើ Java String Object ដោយផ្ទាល់ទេ។ វាប្រើ Serializer ដើម្បីបម្លែងទិន្នន័យទៅជា Bytes មុនពេលបញ្ជូនទៅ Broker។

## 3. Producer គណនា Partition (Partition Calculation)

នៅក្នុង Default Key-Based Partitioning ដែលអ្នកកំពុងប្រើ Producer គណនា Partition តាមរូបមន្ត៖

```
partition = positive(murmur2(keyBytes)) % numberOfPartitions;
```

ការគណនានេះមានជំហានដូចខាងក្រោម៖

Message Key Bytes

"order-001"

Murmur2 Hash

បង្កើតលេខ Hash មួយ

Positive Hash

បម្លែងឱ្យទៅជាលេខមិនអវិជ្ជមាន

Hash % 3

គណនាដើម្បីជ្រើសរើស Partition 0, 1 ឬ 2

### តើ `%` មានន័យដូចម្តេច?

សញ្ញា `%` មានន័យថា សំណល់នៃការចែក (Modulo / Remainder)។

ឧទាហរណ៍ សន្មតថា Positive Hash មានតម្លៃ `100`។ នេះគ្រាន់តែជាលេខឧទាហរណ៍ មិនមែនជា Hash ពិតរបស់ `"order-001"` ទេ។

```
100 ÷ 3 = 33 សល់ 1

100 % 3 = 1

Selected Partition = 1
```

### សាកល្បងគណនា Partition

Partition Calculator

Interactive Demo

Example Positive Hash

Number of Partitions

2

3

4

6

Calculation

# 100 % 3 = 1

Selected Partition: 1

P0

P1

P2

កម្មវិធីគណនានេះប្រើ Positive Hash ឧទាហរណ៍ មិនមែនគណនា Murmur2 Hash ពី Message Key ដោយផ្ទាល់ទេ។

### ឧទាហរណ៍បន្ថែម

| Positive Hash (ឧទាហរណ៍) | ការគណនា       | Partition |
| ----------------------- | ------------- | --------- |
| 99                      | `99 % 3 = 0`  | 0         |
| 100                     | `100 % 3 = 1` | 1         |
| 101                     | `101 % 3 = 2` | 2         |
| 102                     | `102 % 3 = 0` | 0         |

ចំណុចសំខាន់៖

Producer ជ្រើសរើស Partition តែមួយសម្រាប់ Message ទាំងមូល។ វាមិនបែងចែក Message មួយទៅជា 3 ផ្នែកនោះទេ។

## 4. Producer ផ្ញើ Message ទៅ Partition Leader

បន្ទាប់ពី Producer បានគណនា និងជ្រើសរើស Partition រួច វានឹងទទួល Metadata ពី Kafka ដើម្បីស្វែងរក Broker ដែលជា Leader របស់ Partition នោះ។

ឧទាហរណ៍៖

```
Selected Partition: 1
          ↓
Find Leader of Partition 1
          ↓
Send Message to Leader Broker
```

ក្នុងករណីអ្នកប្រើ Kafka Broker តែមួយ Broker នោះអាចធ្វើជា Leader សម្រាប់ Partitions ទាំង 3។

## 5. Broker រក្សាទុក Message និងកំណត់ Offset

នៅពេល Kafka Broker ទទួលបាន Message វានឹងបន្ថែម Record ទៅក្នុង Log របស់ Partition ដែលបានជ្រើសរើស។

ឧទាហរណ៍៖

```
demo-topic
│
├── Partition 0
│
├── Partition 1
│   └── Offset 0: order-001 → "Order created"
│
└── Partition 2
```

ប្រសិនបើ Message បន្ទាប់ត្រូវបានជ្រើសរើសឱ្យទៅ Partition 1 ដដែល វានឹងទទួលបាន Offset បន្ទាប់។

```
Partition 1
│
├── Offset 0: order-001 → "Order created"
│
└── Offset 1: order-001 → "Order paid"
```

ការពន្យល់៖

- Partition គឺជាផ្នែករក្សាទុក Records នៅក្នុង Topic។
- Offset គឺជាលេខសម្គាល់ទីតាំងរបស់ Record ក្នុង Partition។
- Partition នីមួយៗមានលំដាប់ Offset ដាច់ដោយឡែកពីគ្នា។

មានន័យថា Partition 0, Partition 1 និង Partition 2 សុទ្ធតែអាចមាន Offset 0 រៀងៗខ្លួន។

## 6. Kafka បញ្ជាក់ថាការផ្ញើ Message ជោគជ័យ (Acknowledgement)

នៅពេលការផ្ញើ Message បានជោគជ័យ Application នឹង Log លទ្ធផល៖

```
Message published:
topic=demo-topic,
partition=1,
offset=0
```

Log នេះបង្ហាញថា៖

| Field              | អត្ថន័យ                            |
| ------------------ | ---------------------------------- |
| `topic=demo-topic` | ឈ្មោះ Topic ដែលទទួល Message        |
| `partition=1`      | Message ត្រូវបានផ្ញើទៅ Partition 1 |
| `offset=0`         | Record ត្រូវបានរក្សាទុកនៅ Offset 0 |

### លទ្ធផលពី Logs មុនរបស់អ្នក

តាម Logs ដែលអ្នកបានបង្ហាញមុននេះ ការផ្ទុក Messages មានទម្រង់ដូចខាងក្រោម៖

```
demo-topic
│
├── Partition 0
│   └── មិនមាន Message បង្ហាញក្នុង Logs នោះ
│
├── Partition 1
│   └── Offset 0: Third Message
│
└── Partition 2
    ├── Offset 0: First Message
    ├── Offset 1: Second Message
    └── Offset 2: Fourth Message
```

នេះបង្ហាញថា Messages មិនចាំបាច់ត្រូវបានបែងចែកស្មើគ្នាទៅគ្រប់ Partitions នោះទេ។

## 7. ហេតុអ្វី Same Key ទៅ Same Partition?

នៅពេលចំនួន Partitions មិនផ្លាស់ប្ដូរ ហើយប្រើ Partitioning Strategy ដដែល Message Key ដូចគ្នានឹងត្រូវបានជ្រើសរើសទៅ Partition ដូចគ្នា។

ឧទាហរណ៍៖

Producer

Messages with keys

Partition 0

order-001

order-001

Partition 1

order-002

order-005

Partition 2

order-003

order-004

នេះគ្រាន់តែជា Diagram ឧទាហរណ៍ មិនមែនជា Partition Assignment ដែលបានគណនាពិតទេ។

ចំណាំ៖ Key ខុសគ្នាអាចត្រូវបាន Hash ទៅ Partition ដូចគ្នាបាន។ ដូច្នេះ ទោះបី Topic មាន 3 Partitions ក៏មិនមានន័យថា Messages ត្រូវបែងចែកស្មើគ្នា 1/3 ក្នុង Partition នីមួយៗទេ។

## សង្ខេបសម្រាប់ដាក់ក្នុង Presentation Slide

Title: How Kafka Producer Selects a Partition

1\. Client Sends Message

Key: order-001 | Value: Order created

2\. Serialization

Convert Key and Value into Bytes

3\. Partition Calculation

Murmur2 Hash → Positive Hash → Modulo

4\. Send to Partition Leader

Producer finds the leader broker

5\. Store Message & Assign Offset

Append Record to Partition Log

6\. Acknowledge Write

Topic, Partition, Offset

សម្រាប់និយាយនៅមុខថ្នាក់៖

> នៅពេល Client ផ្ញើ Message មក Application, Kafka Producer នឹងបម្លែង Message Key និង Value ទៅជា Bytes។ បន្ទាប់មកវាប្រើ Murmur2 Hash និង Modulo ដើម្បីជ្រើសរើស Partition។ នៅពេលជ្រើសរើសបានហើយ Producer នឹងផ្ញើ Message ទៅ Broker ដែលជា Leader របស់ Partition នោះ។ Broker នឹងរក្សាទុក Message និងកំណត់ Offset។ ចុងក្រោយ Kafka នឹងផ្ញើ Acknowledgement ត្រឡប់មក Producer វិញ ដើម្បីបញ្ជាក់ថាការផ្ញើ Message បានជោគជ័យ។

ចំណុចសំខាន់បំផុត៖ Same Key + Same Partition Count + Same Partitioning Strategy = Same Partition។