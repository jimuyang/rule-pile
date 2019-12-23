/* eslint-disable no-param-reassign */
/* eslint-disable consistent-return */
import React, { useEffect, Fragment } from 'react';
import { Form, Input, Button } from 'antd';
import { withPropsAPI } from 'gg-editor';
import { useImmer } from 'use-immer';
import { inlineFormItemLayout } from './CommonForm';

const { TextArea } = Input;
const InputGroup = Input.Group;
const { Item } = Form;

const StartNodeDetail = props => {
  const { propsAPI } = props;

  const [model, updateModel] = useImmer({
    options: [],
  });

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
    return () => {};
  }, []);

  useEffect(() => {
    const { executeCommand, update, getSelected } = propsAPI;
    const item = getSelected()[0];
    executeCommand(() => {
      update(item, model);
    });
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
      <Item label="input" {...inlineFormItemLayout}>
        <TextArea
          rows={3}
          value={model.input}
          onChange={e => {
            const { value } = e.target;
            updateModel(draft => {
              draft.input = value;
            });
          }}
        />
      </Item>
      <Item label="option" {...inlineFormItemLayout}>
        {model.options.map((item, index) => (
          // eslint-disable-next-line react/no-array-index-key
          <InputGroup compact key={index}>
            <Input
              style={{ width: '45%' }}
              placeholder="key"
              value={item.key}
              onChange={e => {
                const { value } = e.target;
                updateModel(draft => {
                  draft.options[index].key = value;
                });
              }}
            ></Input>
            <Input
              style={{ width: '45%' }}
              placeholder="value"
              value={item.value}
              onChange={e => {
                const { value } = e.target;
                updateModel(draft => {
                  draft.options[index].value = value;
                });
              }}
            ></Input>
            <Button
              onClick={() => {
                updateModel(draft => {
                  draft.options.splice(index, 1);
                });
              }}
              style={{ width: '10%' }}
              icon="delete"
            ></Button>
          </InputGroup>
        ))}
        <Button
          icon="plus"
          type="dashed"
          onClick={() => {
            updateModel(draft => {
              draft.options.push({ key: '', value: '' });
            });
          }}
          block
        >
          添加
        </Button>
      </Item>
    </Fragment>
  );
};

export default Form.create()(withPropsAPI(StartNodeDetail));
