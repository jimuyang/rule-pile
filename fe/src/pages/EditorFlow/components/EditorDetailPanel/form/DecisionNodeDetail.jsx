import React, { Fragment } from 'react';
import { Form, Input } from 'antd';
import { withPropsAPI } from 'gg-editor';
import { inlineFormItemLayout } from './CommonForm';

const { Item } = Form;

class DecisionNodeDetail extends React.Component {
  get item() {
    const { propsAPI } = this.props;
    return propsAPI.getSelected()[0];
  }

  handleSubmit = e => {
    if (e && e.preventDefault) {
      e.preventDefault();
    }
    const { form, propsAPI } = this.props;
    const { getSelected, executeCommand, update } = propsAPI;
    setTimeout(() => {
      form.validateFieldsAndScroll((err, values) => {
        if (err) {
          return;
        }
        const item = getSelected()[0];
        if (!item) {
          return;
        }
        executeCommand(() => {
          update(item, { ...values });
        });
      });
    }, 0);
  };

  render() {
    const { form } = this.props;
    const model = this.item.getModel();
    return (
      <Fragment>
        <Item label="label" {...inlineFormItemLayout}>
          {form.getFieldDecorator('label', {
            initialValue: model.label,
          })(<Input onBlur={this.handleSubmit} />)}
        </Item>
        <Item label="stream" {...inlineFormItemLayout}>
          {form.getFieldDecorator('stream', {
            initialValue: model.stream || 'true',
          })(<Input onBlur={this.handleSubmit} />)}
        </Item>
      </Fragment>
    );
  }
}

export default Form.create()(withPropsAPI(DecisionNodeDetail));
