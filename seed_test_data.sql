BEGIN;

INSERT INTO subjects (name) VALUES
('математика'),
('кыргыз тил'),
('русский'),
('английский'),
('физика'),
('химия'),
('биология'),
('история')
ON CONFLICT (name) DO NOTHING;

INSERT INTO users (created_at, email, full_name, password, phone, role)
SELECT now(), v.email, v.full_name, '$2a$06$SrbhmC4qENXcAcvNPLLIOud4uoOO1IBuPm6OGgBmDV6ndqL9G2A66', v.phone, v.role
FROM (
    VALUES
    ('manager1', 'Айгерим Токтосунова', '+996700111001', 'MANAGER'),
    ('manager2', 'Бектур Жээнбеков', '+996700111002', 'MANAGER'),
    ('teacher_math', 'Нурбек Сыдыков', '+996700111101', 'TEACHER'),
    ('teacher_kyrgyz', 'Айнура Мамбетова', '+996700111102', 'TEACHER'),
    ('teacher_russian', 'Элмира Абдыкадырова', '+996700111103', 'TEACHER'),
    ('teacher_english', 'Азамат Тургунбаев', '+996700111104', 'TEACHER'),
    ('teacher_physics', 'Руслан Калыков', '+996700111105', 'TEACHER'),
    ('teacher_chem', 'Жийдегүл Сариева', '+996700111106', 'TEACHER'),
    ('teacher_bio', 'Гүлнура Осмонова', '+996700111107', 'TEACHER'),
    ('teacher_history', 'Талантбек Шаршенов', '+996700111108', 'TEACHER')
) AS v(email, full_name, phone, role)
WHERE NOT EXISTS (
    SELECT 1 FROM users u WHERE u.email = v.email
);

INSERT INTO groups (created_at, name, start_date, end_date, subject_id, teacher_id)
SELECT now(), v.name, v.start_date, v.end_date, s.id, u.id
FROM (
    VALUES
    ('Математика A1', '2026-03-01'::date, '2026-07-30'::date, 'математика', 'teacher_math'),
    ('Математика B1', '2026-03-15'::date, '2026-08-15'::date, 'математика', 'teacher_math'),
    ('Кыргыз тил A1', '2026-03-05'::date, '2026-07-20'::date, 'кыргыз тил', 'teacher_kyrgyz'),
    ('Русский A1', '2026-03-07'::date, '2026-07-25'::date, 'русский', 'teacher_russian'),
    ('Английский A1', '2026-03-10'::date, '2026-08-01'::date, 'английский', 'teacher_english'),
    ('Физика A1', '2026-03-12'::date, '2026-08-10'::date, 'физика', 'teacher_physics'),
    ('Химия A1', '2026-03-14'::date, '2026-08-12'::date, 'химия', 'teacher_chem'),
    ('Биология A1', '2026-03-16'::date, '2026-08-18'::date, 'биология', 'teacher_bio'),
    ('История A1', '2026-03-18'::date, '2026-08-22'::date, 'история', 'teacher_history')
) AS v(name, start_date, end_date, subject_name, teacher_email)
JOIN subjects s ON s.name = v.subject_name
JOIN users u ON u.email = v.teacher_email
WHERE NOT EXISTS (
    SELECT 1 FROM groups g WHERE g.name = v.name
);

INSERT INTO students (
    created_at, full_name, phone, whatsapp, school, grade, city,
    parent_name, parent_phone, ort_date, status, source, referred_by
)
SELECT now(), v.full_name, v.phone, v.whatsapp, v.school, v.grade, v.city,
       v.parent_name, v.parent_phone, v.ort_date, v.status, v.source, v.referred_by
FROM (
    VALUES
    ('Нурсултан Жолдошев', '+996555000101', '+996555000101', '№61 мектеп', 11, 'Бишкек', 'Канат Жолдошев', '+996555100101', '2026-05-20'::date, 'ACTIVE', 'INSTAGRAM', NULL),
    ('Айпери Садыкова', '+996555000102', '+996555000102', '№67 мектеп', 10, 'Бишкек', 'Гүлмира Садыкова', '+996555100102', '2026-05-22'::date, 'ACTIVE', 'WHATSAPP', NULL),
    ('Бекзат Токтогулов', '+996555000103', '+996555000103', '№13 мектеп', 11, 'Ош', 'Медер Токтогулов', '+996555100103', '2026-05-24'::date, 'AT_RISK', 'TIKTOK', NULL),
    ('Жансая Абдраева', '+996555000104', '+996555000104', '№24 мектеп', 9, 'Бишкек', 'Айжан Абдраева', '+996555100104', '2026-05-24'::date, 'ACTIVE', 'INSTAGRAM', NULL),
    ('Эрбол Нышанов', '+996555000105', '+996555000105', '№70 мектеп', 11, 'Каракол', 'Сапар Нышанов', '+996555100105', '2026-05-26'::date, 'ACTIVE', 'REFERRAL', 'Нурбек Сыдыков'),
    ('Малика Сулайманова', '+996555000106', '+996555000106', '№9 мектеп', 10, 'Бишкек', 'Чынара Сулайманова', '+996555100106', '2026-05-26'::date, 'COMPLETED', 'FACEBOOK', NULL),
    ('Актан Исаков', '+996555000107', '+996555000107', '№42 мектеп', 11, 'Токмок', 'Эрмек Исаков', '+996555100107', '2026-05-28'::date, 'ACTIVE', 'OTHER', NULL),
    ('Алина Жусупова', '+996555000108', '+996555000108', '№29 мектеп', 10, 'Бишкек', 'Раушан Жусупова', '+996555100108', '2026-05-28'::date, 'ACTIVE', 'INSTAGRAM', NULL),
    ('Темирлан Бейшенов', '+996555000109', '+996555000109', '№17 мектеп', 11, 'Ош', 'Улан Бейшенов', '+996555100109', '2026-05-30'::date, 'DROPPED', 'WHATSAPP', NULL),
    ('Гүлайым Асанова', '+996555000110', '+996555000110', '№2 мектеп', 9, 'Нарын', 'Алтынай Асанова', '+996555100110', '2026-06-01'::date, 'ACTIVE', 'REFERRAL', 'Айгерим Токтосунова'),
    ('Элзат Мамыров', '+996555000111', '+996555000111', '№75 мектеп', 11, 'Бишкек', 'Марат Мамыров', '+996555100111', '2026-06-03'::date, 'ACTIVE', 'INSTAGRAM', NULL),
    ('Каныкей Турсунбекова', '+996555000112', '+996555000112', '№35 мектеп', 10, 'Бишкек', 'Назира Турсунбекова', '+996555100112', '2026-06-03'::date, 'AT_RISK', 'TIKTOK', NULL)
) AS v(full_name, phone, whatsapp, school, grade, city, parent_name, parent_phone, ort_date, status, source, referred_by)
WHERE NOT EXISTS (
    SELECT 1 FROM students s WHERE s.full_name = v.full_name AND s.phone = v.phone
);

INSERT INTO student_groups (student_id, group_id)
SELECT s.id, g.id
FROM (
    VALUES
    ('Нурсултан Жолдошев', 'Математика A1'),
    ('Нурсултан Жолдошев', 'Физика A1'),
    ('Айпери Садыкова', 'Кыргыз тил A1'),
    ('Айпери Садыкова', 'История A1'),
    ('Бекзат Токтогулов', 'Математика B1'),
    ('Бекзат Токтогулов', 'Английский A1'),
    ('Жансая Абдраева', 'Русский A1'),
    ('Жансая Абдраева', 'Биология A1'),
    ('Эрбол Нышанов', 'Физика A1'),
    ('Эрбол Нышанов', 'Химия A1'),
    ('Малика Сулайманова', 'Биология A1'),
    ('Малика Сулайманова', 'Химия A1'),
    ('Актан Исаков', 'Математика A1'),
    ('Актан Исаков', 'Английский A1'),
    ('Алина Жусупова', 'Кыргыз тил A1'),
    ('Алина Жусупова', 'Русский A1'),
    ('Темирлан Бейшенов', 'История A1'),
    ('Темирлан Бейшенов', 'Английский A1'),
    ('Гүлайым Асанова', 'Биология A1'),
    ('Гүлайым Асанова', 'Кыргыз тил A1'),
    ('Элзат Мамыров', 'Физика A1'),
    ('Элзат Мамыров', 'Математика B1'),
    ('Каныкей Турсунбекова', 'Русский A1'),
    ('Каныкей Турсунбекова', 'Химия A1')
) AS v(student_name, group_name)
JOIN students s ON s.full_name = v.student_name
JOIN groups g ON g.name = v.group_name
WHERE NOT EXISTS (
    SELECT 1 FROM student_groups sg WHERE sg.student_id = s.id AND sg.group_id = g.id
);

INSERT INTO leads (
    created_at, full_name, phone, whatsapp, parent_name, parent_phone,
    source, status, comment, referred_by, assigned_to_id, next_contact_date
)
SELECT now(), v.full_name, v.phone, v.whatsapp, v.parent_name, v.parent_phone,
       v.source, v.status, v.comment, v.referred_by, u.id, v.next_contact_date
FROM (
    VALUES
    ('Элина Касымова', '+996700200001', '+996700200001', 'Назгүл Касымова', '+996700210001', 'INSTAGRAM', 'NEW', 'Интересуется курсом по математике и английскому.', NULL, 'manager1', '2026-04-15'::date),
    ('Арсен Таалайбеков', '+996700200002', '+996700200002', 'Таалайбек Таалайбеков', '+996700210002', 'WHATSAPP', 'CONTACTED', 'Обещал прийти на пробный урок.', NULL, 'manager2', '2026-04-16'::date),
    ('Мээрим Абылкасымова', '+996700200003', '+996700200003', 'Айдана Абылкасымова', '+996700210003', 'REFERRAL', 'THINKING', 'Сравнивает с другим центром.', 'Айгерим Токтосунова', 'manager1', '2026-04-18'::date),
    ('Кубанычбек Эсенов', '+996700200004', '+996700200004', 'Нургүл Эсенова', '+996700210004', 'TIKTOK', 'PAID', 'Готовится к поступлению в КРСУ.', NULL, 'manager2', '2026-04-14'::date),
    ('Айзаара Мамбетова', '+996700200005', '+996700200005', 'Чынара Мамбетова', '+996700210005', 'FACEBOOK', 'REJECTED', 'Пока отказались из-за графика.', NULL, 'manager1', '2026-04-20'::date),
    ('Русланбек Сагынов', '+996700200006', '+996700200006', 'Жаркынай Сагынова', '+996700210006', 'OTHER', 'NEW', 'Просит скидку на два предмета.', 'Нурбек Сыдыков', 'manager2', '2026-04-17'::date)
) AS v(full_name, phone, whatsapp, parent_name, parent_phone, source, status, comment, referred_by, manager_email, next_contact_date)
JOIN users u ON u.email = v.manager_email
WHERE NOT EXISTS (
    SELECT 1 FROM leads l WHERE l.full_name = v.full_name AND l.phone = v.phone
);

INSERT INTO payments (
    created_at, student_id, amount, total_due, method, status,
    due_date, paid_date, installment_number, total_installments, comment
)
SELECT now(), s.id, v.amount, v.total_due, v.method, v.status,
       v.due_date, v.paid_date, v.installment_number, v.total_installments, v.comment
FROM (
    VALUES
    ('Нурсултан Жолдошев', 12000.00, 12000.00, 'MBANK', 'PAID', '2026-03-05'::date, '2026-03-05'::date, 1, 1, 'Полная оплата за март-июль'),
    ('Айпери Садыкова', 6000.00, 12000.00, 'CASH', 'PENDING', '2026-04-20'::date, NULL, 1, 2, 'Оплатила первую часть'),
    ('Бекзат Токтогулов', 4000.00, 12000.00, 'ODENGI', 'OVERDUE', '2026-04-01'::date, NULL, 1, 3, 'Просрочка по первой оплате'),
    ('Жансая Абдраева', 12000.00, 12000.00, 'ELCART', 'PAID', '2026-03-12'::date, '2026-03-10'::date, 1, 1, 'Ранняя оплата'),
    ('Эрбол Нышанов', 8000.00, 12000.00, 'BANK_TRANSFER', 'PAID', '2026-03-15'::date, '2026-03-16'::date, 1, 2, 'Первая часть оплаты'),
    ('Малика Сулайманова', 12000.00, 12000.00, 'MBANK', 'PAID', '2026-03-18'::date, '2026-03-18'::date, 1, 1, 'Закрыла курс полностью'),
    ('Актан Исаков', 5000.00, 12000.00, 'BALANCE_KG', 'PENDING', '2026-04-25'::date, NULL, 1, 2, 'Ждет перевод от родителей'),
    ('Алина Жусупова', 7000.00, 12000.00, 'CASH', 'PAID', '2026-03-22'::date, '2026-03-22'::date, 1, 2, 'Первый взнос'),
    ('Темирлан Бейшенов', 3000.00, 12000.00, 'OTHER', 'OVERDUE', '2026-03-25'::date, NULL, 1, 4, 'Прекратил посещение'),
    ('Гүлайым Асанова', 12000.00, 12000.00, 'MBANK', 'PAID', '2026-03-28'::date, '2026-03-28'::date, 1, 1, 'Полностью оплачено'),
    ('Элзат Мамыров', 6000.00, 12000.00, 'ODENGI', 'PENDING', '2026-04-30'::date, NULL, 1, 2, 'Первая половина еще не внесена'),
    ('Каныкей Турсунбекова', 6000.00, 12000.00, 'ELCART', 'PAID', '2026-03-30'::date, '2026-03-30'::date, 1, 2, 'Первый взнос принят')
) AS v(student_name, amount, total_due, method, status, due_date, paid_date, installment_number, total_installments, comment)
JOIN students s ON s.full_name = v.student_name
WHERE NOT EXISTS (
    SELECT 1 FROM payments p
    WHERE p.student_id = s.id
      AND p.installment_number = v.installment_number
      AND p.amount = v.amount
);

INSERT INTO mock_exams (created_at, title, exam_date)
SELECT now(), v.title, v.exam_date
FROM (
    VALUES
    ('ОРТ апрель 2026', '2026-04-06'::date),
    ('ОРТ май 2026', '2026-05-04'::date)
) AS v(title, exam_date)
WHERE NOT EXISTS (
    SELECT 1 FROM mock_exams me WHERE me.title = v.title
);

INSERT INTO mock_exam_scores (created_at, mock_exam_id, student_id, subject_id, score)
SELECT now(), me.id, s.id, subj.id, v.score
FROM (
    VALUES
    ('ОРТ апрель 2026', 'Нурсултан Жолдошев', 'математика', 182),
    ('ОРТ апрель 2026', 'Нурсултан Жолдошев', 'физика', 171),
    ('ОРТ апрель 2026', 'Айпери Садыкова', 'кыргыз тил', 164),
    ('ОРТ апрель 2026', 'Айпери Садыкова', 'история', 158),
    ('ОРТ апрель 2026', 'Бекзат Токтогулов', 'математика', 149),
    ('ОРТ апрель 2026', 'Бекзат Токтогулов', 'английский', 141),
    ('ОРТ апрель 2026', 'Жансая Абдраева', 'русский', 173),
    ('ОРТ апрель 2026', 'Жансая Абдраева', 'биология', 167),
    ('ОРТ май 2026', 'Эрбол Нышанов', 'физика', 177),
    ('ОРТ май 2026', 'Эрбол Нышанов', 'химия', 168),
    ('ОРТ май 2026', 'Актан Исаков', 'математика', 159),
    ('ОРТ май 2026', 'Актан Исаков', 'английский', 153),
    ('ОРТ май 2026', 'Алина Жусупова', 'кыргыз тил', 166),
    ('ОРТ май 2026', 'Алина Жусупова', 'русский', 169),
    ('ОРТ май 2026', 'Гүлайым Асанова', 'биология', 174),
    ('ОРТ май 2026', 'Каныкей Турсунбекова', 'химия', 151)
) AS v(exam_title, student_name, subject_name, score)
JOIN mock_exams me ON me.title = v.exam_title
JOIN students s ON s.full_name = v.student_name
JOIN subjects subj ON subj.name = v.subject_name
WHERE NOT EXISTS (
    SELECT 1 FROM mock_exam_scores mes
    WHERE mes.mock_exam_id = me.id
      AND mes.student_id = s.id
      AND mes.subject_id = subj.id
);

INSERT INTO attendance (created_at, lesson_date, present, group_id, student_id)
SELECT now(), v.lesson_date, v.present, g.id, s.id
FROM (
    VALUES
    ('2026-04-01'::date, true, 'Математика A1', 'Нурсултан Жолдошев'),
    ('2026-04-01'::date, true, 'Математика A1', 'Актан Исаков'),
    ('2026-04-02'::date, true, 'Физика A1', 'Нурсултан Жолдошев'),
    ('2026-04-02'::date, false, 'Физика A1', 'Эрбол Нышанов'),
    ('2026-04-03'::date, true, 'Кыргыз тил A1', 'Айпери Садыкова'),
    ('2026-04-03'::date, true, 'Кыргыз тил A1', 'Алина Жусупова'),
    ('2026-04-04'::date, false, 'Русский A1', 'Жансая Абдраева'),
    ('2026-04-04'::date, true, 'Русский A1', 'Каныкей Турсунбекова'),
    ('2026-04-05'::date, true, 'Английский A1', 'Бекзат Токтогулов'),
    ('2026-04-05'::date, false, 'Английский A1', 'Темирлан Бейшенов'),
    ('2026-04-06'::date, true, 'Химия A1', 'Эрбол Нышанов'),
    ('2026-04-06'::date, true, 'Химия A1', 'Каныкей Турсунбекова'),
    ('2026-04-07'::date, true, 'Биология A1', 'Жансая Абдраева'),
    ('2026-04-07'::date, true, 'Биология A1', 'Гүлайым Асанова'),
    ('2026-04-08'::date, true, 'История A1', 'Айпери Садыкова'),
    ('2026-04-08'::date, false, 'История A1', 'Темирлан Бейшенов')
) AS v(lesson_date, present, group_name, student_name)
JOIN groups g ON g.name = v.group_name
JOIN students s ON s.full_name = v.student_name
WHERE NOT EXISTS (
    SELECT 1 FROM attendance a
    WHERE a.student_id = s.id
      AND a.group_id = g.id
      AND a.lesson_date = v.lesson_date
);

COMMIT;
