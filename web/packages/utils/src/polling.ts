type PollingOptions = {
  interval: number;
  maxAttempts?: number;
};

type PollingCallbacks = {
  onStart?: () => void;
  onStop?: () => void;
  onRestart?: () => void;
  onExecute?: (attempt: number) => void;
};

/**
 * 简单轮询控制器。
 *
 * @example
 * ```ts
 * const polling = new Polling(
 *   { interval: 1000, maxAttempts: 3 },
 *   { onExecute: attempt => console.log(attempt) }
 * );
 *
 * polling.start();
 * ```
 */
export class Polling {
  private intervalId: number | null = null;
  private attempts = 0;

  constructor(
    private readonly options: PollingOptions,
    private readonly callbacks: PollingCallbacks = {}
  ) {}

  /**
   * 启动轮询。重复调用不会创建多个定时器。
   *
   * @example
   * ```ts
   * const polling = new Polling({ interval: 1000 });
   * polling.start();
   * ```
   */
  start() {
    if (this.intervalId !== null) {
      return;
    }
    this.attempts = 0;
    this.callbacks.onStart?.();
    this.execute();
    this.intervalId = window.setInterval(() => {
      this.execute();
    }, this.options.interval);
  }

  /**
   * 停止轮询。
   *
   * @example
   * ```ts
   * polling.stop();
   * ```
   */
  stop() {
    if (this.intervalId === null) {
      return;
    }
    window.clearInterval(this.intervalId);
    this.intervalId = null;
    this.callbacks.onStop?.();
  }

  /**
   * 重启轮询，会先停止当前定时器再重新启动。
   *
   * @example
   * ```ts
   * polling.restart();
   * ```
   */
  restart() {
    this.stop();
    this.callbacks.onRestart?.();
    this.start();
  }

  private execute() {
    this.attempts += 1;
    this.callbacks.onExecute?.(this.attempts);
    if (this.options.maxAttempts && this.attempts >= this.options.maxAttempts) {
      this.stop();
    }
  }
}

