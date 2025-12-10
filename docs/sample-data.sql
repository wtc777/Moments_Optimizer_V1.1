-- Sample data for testing Spring Boot APIs (tasks, task_steps, analysis_history, users)
-- Run in MySQL: mysql -h localhost -P 3306 -u moments -pWellrainadmin123 moments_optimizer < docs/sample-data.sql

INSERT INTO users (id, phone, nickname, password_hash, role, credits, created_at, updated_at)
VALUES
  ('test-user-1', '13000000000', 'Test User', 'hashed-password', 'normal', 5, NOW(), NOW())
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname), updated_at = VALUES(updated_at);

INSERT INTO tasks (id, type, status, payload_json, result_json, error_message, created_at, updated_at)
VALUES
  ('task-demo-1', 'moments_optimize', 'SUCCESS',
   '{\"userId\":\"test-user-1\",\"inputText\":\"Sample moment input\",\"imageUrls\":[\"https://example.com/img1.jpg\"]}',
   '{\"summary\":\"Demo result\"}',
   NULL,
   NOW() - INTERVAL 10 MINUTE,
   NOW() - INTERVAL 5 MINUTE)
ON DUPLICATE KEY UPDATE status = VALUES(status), result_json = VALUES(result_json), updated_at = VALUES(updated_at);

INSERT INTO task_steps (task_id, step_order, step_key, step_label, status, started_at, finished_at, extra_json)
VALUES
  ('task-demo-1', 1, 'image_processing', 'Image processing', 'SUCCESS', NOW() - INTERVAL 10 MINUTE, NOW() - INTERVAL 9 MINUTE, NULL),
  ('task-demo-1', 2, 'image_model_call', 'Image model call', 'SUCCESS', NOW() - INTERVAL 9 MINUTE, NOW() - INTERVAL 8 MINUTE, '{\"usage\": {\"input\":10}}'),
  ('task-demo-1', 3, 'image_result_saved', 'Image result saved', 'SUCCESS', NOW() - INTERVAL 8 MINUTE, NOW() - INTERVAL 7 MINUTE, NULL),
  ('task-demo-1', 4, 'prompt_building', 'Prompt building', 'SUCCESS', NOW() - INTERVAL 7 MINUTE, NOW() - INTERVAL 6 MINUTE, NULL),
  ('task-demo-1', 5, 'llm_call', 'LLM call', 'SUCCESS', NOW() - INTERVAL 6 MINUTE, NOW() - INTERVAL 5 MINUTE, '{\"usage\": {\"output\":20}}'),
  ('task-demo-1', 6, 'final_result', 'Final result', 'SUCCESS', NOW() - INTERVAL 5 MINUTE, NOW() - INTERVAL 4 MINUTE, NULL)
ON DUPLICATE KEY UPDATE status = VALUES(status), finished_at = VALUES(finished_at);

INSERT INTO analysis_history (
  id, user_id, created_at, image_path, input_text, output_text,
  duration_ms, input_tokens, output_tokens, total_tokens, model_name, success, error_message
) VALUES (
  'history-demo-1',
  'test-user-1',
  NOW() - INTERVAL 3 MINUTE,
  '/uploads/demo.jpg',
  'Sample history input text',
  'Sample history output text',
  1200,
  50,
  120,
  170,
  'demo-model',
  1,
  NULL
) ON DUPLICATE KEY UPDATE output_text = VALUES(output_text);
