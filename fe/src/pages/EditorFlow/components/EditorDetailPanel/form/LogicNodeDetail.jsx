/* eslint-disable react/no-array-index-key */
/* eslint-disable no-param-reassign */
/* eslint-disable consistent-return */
import React, { useEffect, Fragment } from 'react';
import { Form, Input, Checkbox } from 'antd';
import { withPropsAPI } from 'gg-editor';
import { useImmer } from 'use-immer';
import { inlineFormItemLayout } from './CommonForm';

const { TextArea } = Input;
// const InputGroup = Input.Group;
const { Item } = Form;

const LogicNodeDetail = props => {
  const { propsAPI } = props;

  // const [SelectRuleDef, updateSelectRuleDef] = useState();
  // useEffect(() => {
  //   const isNew = /isNew=true/.test(window.location.hash);
  //   const Comp = isNew
  //     ? createSelectRuleDefinition(
  //         `/ecs.qc/rule/definition/partners?${window.location.hash.split('?')[1]}`,
  //       )
  //     : SelectRuleDefinition;
  //   updateSelectRuleDef(() => Comp);
  // }, []);

  const [model, updateModel] = useImmer({
    logic: [
      {
        type: 1,
        input: 'input',
        code: '',
        invoke: {
          ruleId: '',
          ruleCode: '',
          definitionId: '',
          options: [],
        },
        output: '',
        hit: '',
      },
    ],
  });

  // const updateInvoke = (ruleId, index) =>
  //     http.get('/ecs.qc/rule/definition', {
  //         params: { ruleId },
  //     }).then(({ data }) => {
  //         try {
  //             data.options = JSON.parse(data.options) || [];
  //         } catch (e) {
  //             data.options = [];
  //         }
  //         updateModel(draft => {
  //             draft.logic[index].invoke = pick(data, ['ruleId', 'ruleCode', 'definitionId', 'options']);
  //         });
  //         return data;
  //     });

  useEffect(() => {
    const { getSelected } = propsAPI;
    const item = getSelected()[0];
    updateModel(draft => {
      if (!item) {
        return;
      }
      draft = Object.assign({}, draft, item.getModel());
      return draft;
    });
  }, []);

  useEffect(() => {
    setTimeout(() => {
      const { executeCommand, update, getSelected } = propsAPI;
      const item = getSelected()[0];
      executeCommand(() => {
        update(item, model);
      });
    }, 0);
  }, [model]);

  return (
    <Fragment>
      <Item label="label" {...inlineFormItemLayout}>
        <Input
          value={model.label}
          onChange={e => {
            const { value } = e.target;
            updateModel(draft => {
              draft.label = value;
            });
          }}
        />
      </Item>
      <Item label="stream" {...inlineFormItemLayout}>
        <Input
          value={model.stream}
          onChange={e => {
            const { value } = e.target;
            updateModel(draft => {
              draft.stream = value;
            });
          }}
        />
      </Item>
      {model.logic.map((item, index) => (
        <Fragment key={index}>
          <Item label="codec" {...inlineFormItemLayout}>
            <Checkbox
              checked={item.type === 1}
              onChange={e => {
                const { checked } = e.target;
                updateModel(draft => {
                  draft.logic[index].type = +checked;
                });
              }}
            ></Checkbox>
          </Item>
          {item.type === 0 && (
            <Item label="invoke" {...inlineFormItemLayout}>
              {/* <SelectRuleDef
                                value={item.invoke.ruleId}
                                onChange={(v, option) => {
                                    updateModel(draft => {
                                        draft.logic[index].invoke.ruleId = v;
                                        draft.logic[index].invoke = pick(option.props.raw, [
                                            'ruleId',
                                            'ruleCode',
                                            'definitionId',
                                            'options',
                                        ]);
                                    });
                                }}
                            ></SelectRuleDef> */}
            </Item>
          )}
          <Item label="input" {...inlineFormItemLayout}>
            <Input
              value={item.input}
              onChange={e => {
                const { value } = e.target;
                updateModel(draft => {
                  draft.logic[index].input = value;
                });
              }}
            />
          </Item>
          {item.type === 0 && item.invoke.options.length ? (
            <Item label="option" {...inlineFormItemLayout}>
              {item.invoke.options.map((option, idx) => (
                <Input
                  key={idx}
                  addonBefore={<div style={{ minWidth: 100 }}>{option.key}</div>}
                  onChange={e => {
                    const { value } = e.target;
                    updateModel(draft => {
                      draft.logic[index].invoke.options[idx].value = value;
                    });
                  }}
                  value={option.value}
                />
              ))}
            </Item>
          ) : null}
          {item.type === 1 && (
            <Item label="script" {...inlineFormItemLayout}>
              <TextArea
                rows={3}
                value={item.code}
                onChange={e => {
                  const { value } = e.target;
                  updateModel(draft => {
                    draft.logic[index].code = value;
                  });
                }}
              />
            </Item>
          )}
          <Item label="result" {...inlineFormItemLayout}>
            <Input
              key="output"
              addonBefore={<div style={{ minWidth: 100 }}>output</div>}
              onChange={e => {
                const { value } = e.target;
                updateModel(draft => {
                  draft.logic[index].output = value;
                });
              }}
              value={item.output}
            />
            {item.type === 0 && (
              <Input
                key="hit"
                addonBefore={<div style={{ minWidth: 100 }}>hit</div>}
                onChange={e => {
                  const { value } = e.target;
                  updateModel(draft => {
                    draft.logic[index].hit = value;
                  });
                }}
                value={item.hit}
              />
            )}
          </Item>
        </Fragment>
      ))}
    </Fragment>
  );
};

export default Form.create()(withPropsAPI(LogicNodeDetail));
