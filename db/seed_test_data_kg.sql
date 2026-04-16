-- ORT CRM demo seed for PostgreSQL
-- Run this after the application has started at least once, so system roles already exist.
-- Demo users use bcrypt hash for password: password

BEGIN;

TRUNCATE TABLE
    payment_allocations,
    payment_transactions,
    payment_schedules,
    payment_agreements,
    payments,
    lesson_attendance,
    lessons,
    mock_exam_scores,
    mock_exams,
    referrals,
    student_courses,
    student_groups,
    groups,
    course_subjects,
    leads,
    students,
    courses,
    subjects
RESTART IDENTITY CASCADE;

DELETE FROM users
WHERE email <> 'admin@gmail.com';

INSERT INTO users (email, password, full_name, role_id, phone, created_at)
VALUES
    (
        'manager@ortcrm.kg',
        '$2a$10$7EqJtq98hPqEX7fNZaFWoOePaWxn96p36XHIhj6D/8WkD2.8Q7yW.',
        'Айдана Бекболотова',
        (SELECT id FROM roles WHERE name = 'MANAGER'),
        '+996700100101',
        NOW()
    ),
    (
        'teacher.math@ortcrm.kg',
        '$2a$10$7EqJtq98hPqEX7fNZaFWoOePaWxn96p36XHIhj6D/8WkD2.8Q7yW.',
        'Нурбек Сыдыков',
        (SELECT id FROM roles WHERE name = 'TEACHER'),
        '+996700100102',
        NOW()
    ),
    (
        'teacher.kyrgyz@ortcrm.kg',
        '$2a$10$7EqJtq98hPqEX7fNZaFWoOePaWxn96p36XHIhj6D/8WkD2.8Q7yW.',
        'Жазгүл Турсунова',
        (SELECT id FROM roles WHERE name = 'TEACHER'),
        '+996700100103',
        NOW()
    ),
    (
        'teacher.history@ortcrm.kg',
        '$2a$10$7EqJtq98hPqEX7fNZaFWoOePaWxn96p36XHIhj6D/8WkD2.8Q7yW.',
        'Азамат Абдраимов',
        (SELECT id FROM roles WHERE name = 'TEACHER'),
        '+996700100104',
        NOW()
    );

INSERT INTO subjects (name)
VALUES
    ('Математика'),
    ('Кыргыз тили'),
    ('Тарых'),
    ('Логика');

INSERT INTO courses (name, start_date, end_date, price, status, created_at)
VALUES
    ('ОРТ интенсив жаз 2026', '2026-03-01', '2026-06-30', 18000.00, 'ACTIVE', NOW()),
    ('ОРТ базалык жай 2026', '2026-04-01', '2026-08-31', 22000.00, 'ACTIVE', NOW()),
    ('ОРТ экспресс май 2026', '2026-05-01', '2026-07-15', 15000.00, 'PLANNED', NOW());

INSERT INTO course_subjects (course_id, subject_id, teacher_id, created_at)
VALUES
    (
        (SELECT id FROM courses WHERE name = 'ОРТ интенсив жаз 2026'),
        (SELECT id FROM subjects WHERE name = 'Математика'),
        (SELECT id FROM users WHERE email = 'teacher.math@ortcrm.kg'),
        NOW()
    ),
    (
        (SELECT id FROM courses WHERE name = 'ОРТ интенсив жаз 2026'),
        (SELECT id FROM subjects WHERE name = 'Кыргыз тили'),
        (SELECT id FROM users WHERE email = 'teacher.kyrgyz@ortcrm.kg'),
        NOW()
    ),
    (
        (SELECT id FROM courses WHERE name = 'ОРТ базалык жай 2026'),
        (SELECT id FROM subjects WHERE name = 'Математика'),
        (SELECT id FROM users WHERE email = 'teacher.math@ortcrm.kg'),
        NOW()
    ),
    (
        (SELECT id FROM courses WHERE name = 'ОРТ базалык жай 2026'),
        (SELECT id FROM subjects WHERE name = 'Тарых'),
        (SELECT id FROM users WHERE email = 'teacher.history@ortcrm.kg'),
        NOW()
    ),
    (
        (SELECT id FROM courses WHERE name = 'ОРТ экспресс май 2026'),
        (SELECT id FROM subjects WHERE name = 'Логика'),
        (SELECT id FROM users WHERE email = 'teacher.math@ortcrm.kg'),
        NOW()
    );

INSERT INTO groups (name, subject_id, teacher_id, start_date, end_date, created_at)
VALUES
    (
        'Математика A1',
        (SELECT id FROM subjects WHERE name = 'Математика'),
        (SELECT id FROM users WHERE email = 'teacher.math@ortcrm.kg'),
        '2026-03-01',
        '2026-06-30',
        NOW()
    ),
    (
        'Кыргыз тили K1',
        (SELECT id FROM subjects WHERE name = 'Кыргыз тили'),
        (SELECT id FROM users WHERE email = 'teacher.kyrgyz@ortcrm.kg'),
        '2026-03-01',
        '2026-06-30',
        NOW()
    ),
    (
        'Тарых T1',
        (SELECT id FROM subjects WHERE name = 'Тарых'),
        (SELECT id FROM users WHERE email = 'teacher.history@ortcrm.kg'),
        '2026-04-01',
        '2026-08-31',
        NOW()
    ),
    (
        'Логика L1',
        (SELECT id FROM subjects WHERE name = 'Логика'),
        (SELECT id FROM users WHERE email = 'teacher.math@ortcrm.kg'),
        '2026-05-01',
        '2026-07-15',
        NOW()
    );

INSERT INTO students (
    full_name,
    phone,
    whatsapp,
    school,
    grade,
    city,
    parent_name,
    parent_phone,
    ort_date,
    status,
    source,
    referred_by_student_id,
    created_at
)
VALUES
    ('Арууке Сатыбалдиева', '+996555110001', '+996555110001', '№61 мектеп', 11, 'Бишкек', 'Гүлмира Сатыбалдиева', '+996555210001', '2026-05-20', 'ACTIVE', 'INSTAGRAM', NULL, NOW()),
    ('Бекзат Иманкулов', '+996555110002', '+996555110002', '№13 мектеп', 10, 'Ош', 'Кубаныч Иманкулов', '+996555210002', '2026-05-20', 'ACTIVE', 'WHATSAPP', NULL, NOW()),
    ('Нурай Токторбаева', '+996555110003', '+996555110003', '№24 гимназия', 11, 'Бишкек', 'Айнагүл Токторбаева', '+996555210003', '2026-05-21', 'AT_RISK', 'TIKTOK', NULL, NOW()),
    ('Элдар Жумабаев', '+996555110004', '+996555110004', '№70 мектеп', 11, 'Каракол', 'Руслан Жумабаев', '+996555210004', '2026-05-21', 'ACTIVE', 'REFERRAL', 1, NOW()),
    ('Айпери Кожомкулова', '+996555110005', '+996555110005', '№6 мектеп', 9, 'Талас', 'Салтанат Кожомкулова', '+996555210005', '2026-05-22', 'ACTIVE', 'INSTAGRAM', NULL, NOW()),
    ('Темирлан Абылкасымов', '+996555110006', '+996555110006', '№17 мектеп', 11, 'Нарын', 'Жылдыз Абылкасымова', '+996555210006', '2026-05-22', 'COMPLETED', 'FACEBOOK', NULL, NOW()),
    ('Камила Осмонова', '+996555110007', '+996555110007', '№4 лицей', 10, 'Бишкек', 'Мээрим Осмонова', '+996555210007', '2026-05-23', 'DROPPED', 'OTHER', NULL, NOW()),
    ('Уланбек Мамытов', '+996555110008', '+996555110008', '№45 мектеп', 11, 'Жалал-Абад', 'Айнура Мамытова', '+996555210008', '2026-05-23', 'ACTIVE', 'WHATSAPP', NULL, NOW());

UPDATE students
SET referred_by_student_id = (SELECT id FROM students WHERE full_name = 'Арууке Сатыбалдиева')
WHERE full_name = 'Элдар Жумабаев';

INSERT INTO student_groups (student_id, group_id)
VALUES
    ((SELECT id FROM students WHERE full_name = 'Арууке Сатыбалдиева'), (SELECT id FROM groups WHERE name = 'Математика A1')),
    ((SELECT id FROM students WHERE full_name = 'Арууке Сатыбалдиева'), (SELECT id FROM groups WHERE name = 'Кыргыз тили K1')),
    ((SELECT id FROM students WHERE full_name = 'Бекзат Иманкулов'), (SELECT id FROM groups WHERE name = 'Математика A1')),
    ((SELECT id FROM students WHERE full_name = 'Бекзат Иманкулов'), (SELECT id FROM groups WHERE name = 'Тарых T1')),
    ((SELECT id FROM students WHERE full_name = 'Нурай Токторбаева'), (SELECT id FROM groups WHERE name = 'Кыргыз тили K1')),
    ((SELECT id FROM students WHERE full_name = 'Нурай Токторбаева'), (SELECT id FROM groups WHERE name = 'Тарых T1')),
    ((SELECT id FROM students WHERE full_name = 'Элдар Жумабаев'), (SELECT id FROM groups WHERE name = 'Математика A1')),
    ((SELECT id FROM students WHERE full_name = 'Айпери Кожомкулова'), (SELECT id FROM groups WHERE name = 'Логика L1')),
    ((SELECT id FROM students WHERE full_name = 'Темирлан Абылкасымов'), (SELECT id FROM groups WHERE name = 'Математика A1')),
    ((SELECT id FROM students WHERE full_name = 'Уланбек Мамытов'), (SELECT id FROM groups WHERE name = 'Тарых T1'));

INSERT INTO student_courses (
    student_id,
    course_id,
    course_price,
    final_price,
    discount_amount,
    referral_discount_amount,
    status,
    created_at
)
VALUES
    (
        (SELECT id FROM students WHERE full_name = 'Арууке Сатыбалдиева'),
        (SELECT id FROM courses WHERE name = 'ОРТ интенсив жаз 2026'),
        18000.00,
        18000.00,
        0.00,
        0.00,
        'ACTIVE',
        NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Бекзат Иманкулов'),
        (SELECT id FROM courses WHERE name = 'ОРТ базалык жай 2026'),
        22000.00,
        21600.00,
        400.00,
        0.00,
        'ACTIVE',
        NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Нурай Токторбаева'),
        (SELECT id FROM courses WHERE name = 'ОРТ базалык жай 2026'),
        22000.00,
        19000.00,
        2000.00,
        1000.00,
        'ACTIVE',
        NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Элдар Жумабаев'),
        (SELECT id FROM courses WHERE name = 'ОРТ интенсив жаз 2026'),
        18000.00,
        17000.00,
        0.00,
        1000.00,
        'ACTIVE',
        NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Айпери Кожомкулова'),
        (SELECT id FROM courses WHERE name = 'ОРТ экспресс май 2026'),
        15000.00,
        14500.00,
        500.00,
        0.00,
        'ACTIVE',
        NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Темирлан Абылкасымов'),
        (SELECT id FROM courses WHERE name = 'ОРТ интенсив жаз 2026'),
        18000.00,
        18000.00,
        0.00,
        0.00,
        'COMPLETED',
        NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Уланбек Мамытов'),
        (SELECT id FROM courses WHERE name = 'ОРТ базалык жай 2026'),
        22000.00,
        21000.00,
        1000.00,
        0.00,
        'ACTIVE',
        NOW()
    );

INSERT INTO payment_agreements (
    student_course_id,
    type,
    total_amount,
    first_due_date,
    months_count,
    billing_day,
    status,
    created_at
)
VALUES
    (
        (SELECT sc.id FROM student_courses sc JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Арууке Сатыбалдиева'),
        'FULL',
        18000.00,
        '2026-03-05',
        1,
        5,
        'COMPLETED',
        NOW()
    ),
    (
        (SELECT sc.id FROM student_courses sc JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Бекзат Иманкулов'),
        'INSTALLMENT',
        21600.00,
        '2026-04-10',
        3,
        10,
        'ACTIVE',
        NOW()
    ),
    (
        (SELECT sc.id FROM student_courses sc JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Нурай Токторбаева'),
        'INSTALLMENT',
        19000.00,
        '2026-03-15',
        2,
        15,
        'ACTIVE',
        NOW()
    ),
    (
        (SELECT sc.id FROM student_courses sc JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Элдар Жумабаев'),
        'INSTALLMENT',
        17000.00,
        '2026-04-08',
        2,
        8,
        'ACTIVE',
        NOW()
    ),
    (
        (SELECT sc.id FROM student_courses sc JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Темирлан Абылкасымов'),
        'FULL',
        18000.00,
        '2026-02-28',
        1,
        28,
        'COMPLETED',
        NOW()
    );

INSERT INTO payment_schedules (
    agreement_id,
    installment_number,
    amount_due,
    paid_amount,
    due_date,
    paid_date,
    status,
    comment,
    created_at
)
VALUES
    (
        (SELECT pa.id FROM payment_agreements pa
         JOIN student_courses sc ON sc.id = pa.student_course_id
         JOIN students s ON s.id = sc.student_id
         WHERE s.full_name = 'Арууке Сатыбалдиева'),
        1, 18000.00, 18000.00, '2026-03-05', '2026-03-04', 'PAID', 'Толук төлөндү', NOW()
    ),
    (
        (SELECT pa.id FROM payment_agreements pa
         JOIN student_courses sc ON sc.id = pa.student_course_id
         JOIN students s ON s.id = sc.student_id
         WHERE s.full_name = 'Бекзат Иманкулов'),
        1, 7200.00, 7200.00, '2026-04-10', '2026-04-10', 'PAID', 'Биринчи бөлүк', NOW()
    ),
    (
        (SELECT pa.id FROM payment_agreements pa
         JOIN student_courses sc ON sc.id = pa.student_course_id
         JOIN students s ON s.id = sc.student_id
         WHERE s.full_name = 'Бекзат Иманкулов'),
        2, 7200.00, 3600.00, '2026-05-10', '2026-05-12', 'PARTIAL', 'Жарымы төлөндү', NOW()
    ),
    (
        (SELECT pa.id FROM payment_agreements pa
         JOIN student_courses sc ON sc.id = pa.student_course_id
         JOIN students s ON s.id = sc.student_id
         WHERE s.full_name = 'Бекзат Иманкулов'),
        3, 7200.00, 0.00, '2026-06-10', NULL, 'PENDING', 'Кийинки төлөм', NOW()
    ),
    (
        (SELECT pa.id FROM payment_agreements pa
         JOIN student_courses sc ON sc.id = pa.student_course_id
         JOIN students s ON s.id = sc.student_id
         WHERE s.full_name = 'Нурай Токторбаева'),
        1, 9500.00, 9500.00, '2026-03-15', '2026-03-16', 'PAID', 'Биринчи ай', NOW()
    ),
    (
        (SELECT pa.id FROM payment_agreements pa
         JOIN student_courses sc ON sc.id = pa.student_course_id
         JOIN students s ON s.id = sc.student_id
         WHERE s.full_name = 'Нурай Токторбаева'),
        2, 9500.00, 0.00, '2026-04-15', NULL, 'OVERDUE', 'Кечиккен төлөм', NOW()
    ),
    (
        (SELECT pa.id FROM payment_agreements pa
         JOIN student_courses sc ON sc.id = pa.student_course_id
         JOIN students s ON s.id = sc.student_id
         WHERE s.full_name = 'Элдар Жумабаев'),
        1, 8500.00, 8500.00, '2026-04-08', '2026-04-07', 'PAID', 'Биринчи төлөм', NOW()
    ),
    (
        (SELECT pa.id FROM payment_agreements pa
         JOIN student_courses sc ON sc.id = pa.student_course_id
         JOIN students s ON s.id = sc.student_id
         WHERE s.full_name = 'Элдар Жумабаев'),
        2, 8500.00, 0.00, '2026-05-08', NULL, 'PENDING', 'Экинчи төлөм', NOW()
    ),
    (
        (SELECT pa.id FROM payment_agreements pa
         JOIN student_courses sc ON sc.id = pa.student_course_id
         JOIN students s ON s.id = sc.student_id
         WHERE s.full_name = 'Темирлан Абылкасымов'),
        1, 18000.00, 18000.00, '2026-02-28', '2026-02-27', 'PAID', 'Курс жабылган', NOW()
    );

INSERT INTO payment_transactions (student_id, agreement_id, amount, paid_at, method, comment, created_at)
VALUES
    (
        (SELECT id FROM students WHERE full_name = 'Арууке Сатыбалдиева'),
        (SELECT pa.id FROM payment_agreements pa JOIN student_courses sc ON sc.id = pa.student_course_id JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Арууке Сатыбалдиева'),
        18000.00, '2026-03-04', 'MBANK', 'Толук төлөм', NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Бекзат Иманкулов'),
        (SELECT pa.id FROM payment_agreements pa JOIN student_courses sc ON sc.id = pa.student_course_id JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Бекзат Иманкулов'),
        7200.00, '2026-04-10', 'CASH', '1-бөлүк', NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Бекзат Иманкулов'),
        (SELECT pa.id FROM payment_agreements pa JOIN student_courses sc ON sc.id = pa.student_course_id JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Бекзат Иманкулов'),
        3600.00, '2026-05-12', 'ELCART', 'Жарым төлөм', NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Нурай Токторбаева'),
        (SELECT pa.id FROM payment_agreements pa JOIN student_courses sc ON sc.id = pa.student_course_id JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Нурай Токторбаева'),
        9500.00, '2026-03-16', 'ODENGI', '1-ай төлөндү', NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Элдар Жумабаев'),
        (SELECT pa.id FROM payment_agreements pa JOIN student_courses sc ON sc.id = pa.student_course_id JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Элдар Жумабаев'),
        8500.00, '2026-04-07', 'MBANK', 'Реферал менен келген студент', NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Темирлан Абылкасымов'),
        (SELECT pa.id FROM payment_agreements pa JOIN student_courses sc ON sc.id = pa.student_course_id JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Темирлан Абылкасымов'),
        18000.00, '2026-02-27', 'BANK_TRANSFER', 'Толук жабылды', NOW()
    );

INSERT INTO payment_allocations (transaction_id, schedule_id, allocated_amount)
VALUES
    (
        (SELECT pt.id FROM payment_transactions pt JOIN students s ON s.id = pt.student_id WHERE s.full_name = 'Арууке Сатыбалдиева' AND pt.amount = 18000.00),
        (SELECT ps.id FROM payment_schedules ps JOIN payment_agreements pa ON pa.id = ps.agreement_id JOIN student_courses sc ON sc.id = pa.student_course_id JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Арууке Сатыбалдиева' AND ps.installment_number = 1),
        18000.00
    ),
    (
        (SELECT pt.id FROM payment_transactions pt JOIN students s ON s.id = pt.student_id WHERE s.full_name = 'Бекзат Иманкулов' AND pt.amount = 7200.00),
        (SELECT ps.id FROM payment_schedules ps JOIN payment_agreements pa ON pa.id = ps.agreement_id JOIN student_courses sc ON sc.id = pa.student_course_id JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Бекзат Иманкулов' AND ps.installment_number = 1),
        7200.00
    ),
    (
        (SELECT pt.id FROM payment_transactions pt JOIN students s ON s.id = pt.student_id WHERE s.full_name = 'Бекзат Иманкулов' AND pt.amount = 3600.00),
        (SELECT ps.id FROM payment_schedules ps JOIN payment_agreements pa ON pa.id = ps.agreement_id JOIN student_courses sc ON sc.id = pa.student_course_id JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Бекзат Иманкулов' AND ps.installment_number = 2),
        3600.00
    ),
    (
        (SELECT pt.id FROM payment_transactions pt JOIN students s ON s.id = pt.student_id WHERE s.full_name = 'Нурай Токторбаева' AND pt.amount = 9500.00),
        (SELECT ps.id FROM payment_schedules ps JOIN payment_agreements pa ON pa.id = ps.agreement_id JOIN student_courses sc ON sc.id = pa.student_course_id JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Нурай Токторбаева' AND ps.installment_number = 1),
        9500.00
    ),
    (
        (SELECT pt.id FROM payment_transactions pt JOIN students s ON s.id = pt.student_id WHERE s.full_name = 'Элдар Жумабаев' AND pt.amount = 8500.00),
        (SELECT ps.id FROM payment_schedules ps JOIN payment_agreements pa ON pa.id = ps.agreement_id JOIN student_courses sc ON sc.id = pa.student_course_id JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Элдар Жумабаев' AND ps.installment_number = 1),
        8500.00
    ),
    (
        (SELECT pt.id FROM payment_transactions pt JOIN students s ON s.id = pt.student_id WHERE s.full_name = 'Темирлан Абылкасымов' AND pt.amount = 18000.00),
        (SELECT ps.id FROM payment_schedules ps JOIN payment_agreements pa ON pa.id = ps.agreement_id JOIN student_courses sc ON sc.id = pa.student_course_id JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Темирлан Абылкасымов' AND ps.installment_number = 1),
        18000.00
    );

INSERT INTO payments (
    student_id,
    amount,
    total_due,
    method,
    status,
    due_date,
    paid_date,
    installment_number,
    total_installments,
    comment,
    created_at
)
VALUES
    (
        (SELECT id FROM students WHERE full_name = 'Арууке Сатыбалдиева'),
        18000.00, 18000.00, 'MBANK', 'PAID', '2026-03-05', '2026-03-04', 1, 1, 'Legacy payment row', NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Бекзат Иманкулов'),
        7200.00, 7200.00, 'CASH', 'PAID', '2026-04-10', '2026-04-10', 1, 3, 'Legacy installment 1', NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Бекзат Иманкулов'),
        3600.00, 7200.00, 'ELCART', 'PENDING', '2026-05-10', NULL, 2, 3, 'Legacy installment 2', NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Нурай Токторбаева'),
        0.00, 9500.00, 'ODENGI', 'OVERDUE', '2026-04-15', NULL, 2, 2, 'Legacy overdue row', NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Элдар Жумабаев'),
        8500.00, 8500.00, 'MBANK', 'PAID', '2026-04-08', '2026-04-07', 1, 2, 'Legacy installment 1', NOW()
    ),
    (
        (SELECT id FROM students WHERE full_name = 'Уланбек Мамытов'),
        0.00, 10500.00, 'OTHER', 'PENDING', '2026-05-20', NULL, 1, 2, 'Legacy planned row', NOW()
    );

INSERT INTO referrals (
    referrer_student_id,
    referred_student_id,
    reward_amount,
    remaining_amount,
    status,
    available_at,
    applied_at,
    created_at
)
VALUES
    (
        (SELECT id FROM students WHERE full_name = 'Арууке Сатыбалдиева'),
        (SELECT id FROM students WHERE full_name = 'Элдар Жумабаев'),
        1000.00,
        1000.00,
        'AVAILABLE',
        NOW(),
        NULL,
        NOW()
    );

INSERT INTO mock_exams (title, exam_date, created_at)
VALUES
    ('ОРТ сыноо апрель', '2026-04-20', NOW()),
    ('ОРТ сыноо май', '2026-05-18', NOW());

INSERT INTO mock_exam_scores (mock_exam_id, student_id, subject_id, score, created_at)
VALUES
    (
        (SELECT id FROM mock_exams WHERE title = 'ОРТ сыноо апрель'),
        (SELECT id FROM students WHERE full_name = 'Арууке Сатыбалдиева'),
        (SELECT id FROM subjects WHERE name = 'Математика'),
        178,
        NOW()
    ),
    (
        (SELECT id FROM mock_exams WHERE title = 'ОРТ сыноо апрель'),
        (SELECT id FROM students WHERE full_name = 'Арууке Сатыбалдиева'),
        (SELECT id FROM subjects WHERE name = 'Кыргыз тили'),
        164,
        NOW()
    ),
    (
        (SELECT id FROM mock_exams WHERE title = 'ОРТ сыноо апрель'),
        (SELECT id FROM students WHERE full_name = 'Бекзат Иманкулов'),
        (SELECT id FROM subjects WHERE name = 'Математика'),
        151,
        NOW()
    ),
    (
        (SELECT id FROM mock_exams WHERE title = 'ОРТ сыноо апрель'),
        (SELECT id FROM students WHERE full_name = 'Нурай Токторбаева'),
        (SELECT id FROM subjects WHERE name = 'Тарых'),
        142,
        NOW()
    ),
    (
        (SELECT id FROM mock_exams WHERE title = 'ОРТ сыноо май'),
        (SELECT id FROM students WHERE full_name = 'Арууке Сатыбалдиева'),
        (SELECT id FROM subjects WHERE name = 'Математика'),
        184,
        NOW()
    ),
    (
        (SELECT id FROM mock_exams WHERE title = 'ОРТ сыноо май'),
        (SELECT id FROM students WHERE full_name = 'Бекзат Иманкулов'),
        (SELECT id FROM subjects WHERE name = 'Тарых'),
        149,
        NOW()
    ),
    (
        (SELECT id FROM mock_exams WHERE title = 'ОРТ сыноо май'),
        (SELECT id FROM students WHERE full_name = 'Элдар Жумабаев'),
        (SELECT id FROM subjects WHERE name = 'Математика'),
        159,
        NOW()
    ),
    (
        (SELECT id FROM mock_exams WHERE title = 'ОРТ сыноо май'),
        (SELECT id FROM students WHERE full_name = 'Айпери Кожомкулова'),
        (SELECT id FROM subjects WHERE name = 'Логика'),
        167,
        NOW()
    );

INSERT INTO lessons (course_subject_id, lesson_date, topic, status, created_at)
VALUES
    (
        (SELECT cs.id FROM course_subjects cs
         JOIN courses c ON c.id = cs.course_id
         JOIN subjects s ON s.id = cs.subject_id
         WHERE c.name = 'ОРТ интенсив жаз 2026' AND s.name = 'Математика'),
        '2026-04-02',
        'Теңдемелер',
        'COMPLETED',
        NOW()
    ),
    (
        (SELECT cs.id FROM course_subjects cs
         JOIN courses c ON c.id = cs.course_id
         JOIN subjects s ON s.id = cs.subject_id
         WHERE c.name = 'ОРТ интенсив жаз 2026' AND s.name = 'Кыргыз тили'),
        '2026-04-03',
        'Текст менен иштөө',
        'COMPLETED',
        NOW()
    ),
    (
        (SELECT cs.id FROM course_subjects cs
         JOIN courses c ON c.id = cs.course_id
         JOIN subjects s ON s.id = cs.subject_id
         WHERE c.name = 'ОРТ базалык жай 2026' AND s.name = 'Тарых'),
        '2026-04-12',
        'Кыргыз хандыгы',
        'COMPLETED',
        NOW()
    ),
    (
        (SELECT cs.id FROM course_subjects cs
         JOIN courses c ON c.id = cs.course_id
         JOIN subjects s ON s.id = cs.subject_id
         WHERE c.name = 'ОРТ экспресс май 2026' AND s.name = 'Логика'),
        '2026-05-05',
        'Логикалык катарлар',
        'SCHEDULED',
        NOW()
    );

INSERT INTO lesson_attendance (lesson_id, student_course_id, present, created_at)
VALUES
    (
        (SELECT id FROM lessons WHERE topic = 'Теңдемелер'),
        (SELECT sc.id FROM student_courses sc JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Арууке Сатыбалдиева'),
        TRUE,
        NOW()
    ),
    (
        (SELECT id FROM lessons WHERE topic = 'Теңдемелер'),
        (SELECT sc.id FROM student_courses sc JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Бекзат Иманкулов'),
        TRUE,
        NOW()
    ),
    (
        (SELECT id FROM lessons WHERE topic = 'Текст менен иштөө'),
        (SELECT sc.id FROM student_courses sc JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Арууке Сатыбалдиева'),
        TRUE,
        NOW()
    ),
    (
        (SELECT id FROM lessons WHERE topic = 'Кыргыз хандыгы'),
        (SELECT sc.id FROM student_courses sc JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Бекзат Иманкулов'),
        TRUE,
        NOW()
    ),
    (
        (SELECT id FROM lessons WHERE topic = 'Кыргыз хандыгы'),
        (SELECT sc.id FROM student_courses sc JOIN students s ON s.id = sc.student_id WHERE s.full_name = 'Нурай Токторбаева'),
        FALSE,
        NOW()
    );

INSERT INTO leads (
    full_name,
    phone,
    whatsapp,
    parent_name,
    parent_phone,
    source,
    status,
    comment,
    referred_by,
    assigned_to_id,
    next_contact_date,
    created_at
)
VALUES
    (
        'Каныкей Эсенова',
        '+996700300001',
        '+996700300001',
        'Элвира Эсенова',
        '+996700400001',
        'INSTAGRAM',
        'NEW',
        'Математика боюнча кызыгуу бар',
        NULL,
        (SELECT id FROM users WHERE email = 'manager@ortcrm.kg'),
        '2026-04-20',
        NOW()
    ),
    (
        'Байэл Шаршенбеков',
        '+996700300002',
        '+996700300002',
        'Нургүл Шаршенбекова',
        '+996700400002',
        'REFERRAL',
        'CONTACTED',
        'Арууке аркылуу келген',
        'Арууке Сатыбалдиева',
        (SELECT id FROM users WHERE email = 'manager@ortcrm.kg'),
        '2026-04-18',
        NOW()
    ),
    (
        'Мээрим Карыпбек кызы',
        '+996700300003',
        '+996700300003',
        'Жыпар Карыпбекова',
        '+996700400003',
        'WHATSAPP',
        'THINKING',
        'Ата-энеси дагы ойлонуп жатат',
        NULL,
        (SELECT id FROM users WHERE email = 'manager@ortcrm.kg'),
        '2026-04-22',
        NOW()
    ),
    (
        'Арслан Тилек уулу',
        '+996700300004',
        '+996700300004',
        'Айжан Тилекова',
        '+996700400004',
        'TIKTOK',
        'PAID',
        'Кийинки жумада студентке айланат',
        NULL,
        (SELECT id FROM users WHERE email = 'manager@ortcrm.kg'),
        '2026-04-19',
        NOW()
    );

COMMIT;
